package lettuce.demo.Controller;


import jakarta.mail.MessagingException;
import lettuce.demo.Member.AuthForm;
import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberRepository memberRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final MailService mailService;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member")Member member){
        return "members/addMemberForm";
    }
    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult result, Model model) {

        var memberDB = memberRepository.findByEmail(member.getEmail());

        if(memberDB.isPresent()) {
            if (!memberDB.get().getVerified()) {
                memberRepository.delete(memberDB.get());
            } else {
                result.rejectValue("email", "duplicate.email", "이미 사용중인 이메일입니다.");
            }
        }
        if(memberRepository.findByNickname(member.getNickname()).isPresent()) {
            result.rejectValue("nickname", "duplicate.nickname", "이미 사용중인 닉네임입니다.");
        }
        if (!member.getPassword().equals(member.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "mismatch.passwordConfirm", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (result.hasErrors()) {
            return "members/addMemberForm";
        }


        member.setPassword(passwordEncoder.encode(member.getPassword()));
        model.addAttribute("email",member.getEmail());
        model.addAttribute("nickname",member.getNickname());
        model.addAttribute("authNum",member.getAuthNum());
        String authNum = mailService.createEmailCode();
        member.setVerified(false);
        member.setAuthNum(authNum);
        memberRepository.save(member);
        try {
            mailService.createEmailForm(member.getEmail(),member.getName(),authNum);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/members/auth?email=" + member.getEmail();
    }

    @GetMapping("/auth")
    public String authForm(@RequestParam("email") String email, Model model){
        model.addAttribute("email",email);
        model.addAttribute("auth",new AuthForm());
        return "members/authForm";
    }

    @PostMapping("/auth")
    public String auth(@RequestParam("email") String email, @ModelAttribute("auth") @Validated AuthForm authForm, BindingResult result,Model model){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 사용하는 회원이 없습니다."));
        if (!member.getAuthNum().equals(authForm.getAuthNum())) {
            result.rejectValue("authNum", "mismatch.authNum", "인증번호가 일치하지 않습니다.");
        }
        if (result.hasErrors()) {
            model.addAttribute("email", email);
            return "members/authForm";
        }

        member.setVerified(true);
        System.out.println("member = " + member);
        memberRepository.save(member);
        return "redirect:/";
    }

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody String location, Principal principal) {
        String username = principal.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            String[] splitLocation = location.split(" ");
            String simplifiedLocation = String.join(" ", Arrays.copyOfRange(splitLocation, 0, 2));
            member.setLocation(simplifiedLocation);
            memberRepository.save(member);
            System.out.println("location = " + simplifiedLocation);
            return ResponseEntity.ok("Location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found.");
        }
    }
}