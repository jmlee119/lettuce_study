package lettuce.demo.Controller;

import jakarta.mail.MessagingException;
import lettuce.demo.Member.Member;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/member")
public class LoginController {
    private final PasswordEncoder passwordEncoder;
    private JavaMailSender javaMailSender;
    public LoginController(PasswordEncoder passwordEncoder, JavaMailSender javaMailSender){
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }
    @Autowired
    private MemberRepository memberRepository;



    @GetMapping("/login")
    public String login(){
        return "login/login";
    }
    @GetMapping("/findId")
    public String findIdForm(){
        return "members/findIdForm";
    }

    @PostMapping("/findId")
    public String findId(@RequestParam("nickname") String nickname,@RequestParam("phone") String phone, Model model) throws MessagingException{
        Optional<Member> findnick = memberRepository.findByNickname(nickname);
        System.out.println("findnick.get().getPhone() = " + findnick.get().getPhone());
        System.out.println("findnick.get().getPhone() = " + findnick.get().getNickname());
        if(findnick.isPresent() && findnick.get().getPhone().equals(phone)){
            Member member = findnick.get();
            String youremail = member.getEmail();
            String yourname = member.getName();
            model.addAttribute("message",yourname+"의 이메일은 "+ youremail + "입니다.");
        }
        else model.addAttribute("errorMessage","해당 이메일을 사용하시는 회원이 없습니다.");

        return "members/findIdResult";
    }

    @GetMapping("/findPassword")
    public String findPassword(){return "members/findPassword";}

    @PostMapping("/findPassword")
<<<<<<< HEAD
    public String findPassword(@RequestParam("email") String email, Model model){
        Optional<Member> findemail = memberRepository.findByEmail(email);
        if(findemail.isPresent()){
            Member member = findemail.get();
            String yourname = member.getName();
            String newPassword = UUID.randomUUID().toString().substring(0,10);
            MailService mailService = new MailService(javaMailSender);
            try {
                mailService.findPasswordForm(email, yourname, newPassword);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
=======
    public String findPassword(@RequestParam("email") String email,@RequestParam("phone") String phone, Model model, HttpSession httpSession){
        Optional<Member> findemail = memberRepository.findByEmail(email);
        // Optional<Member> findPhone = memberRepository.findByPhone(phone);
        if(findemail.isPresent() && findemail.get().getPhone().equals(phone)){
            if(findemail.get().getVerified() == true){
                Member member = findemail.get();
                String yourname = member.getName();
                String authNum = UUID.randomUUID().toString().substring(0,10);
                MailService mailService = new MailService(javaMailSender);
                try {
                    mailService.createEmailForm(email, yourname, authNum);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                httpSession.setAttribute("authNum",authNum);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        httpSession.removeAttribute("authNum");
                        System.out.println("authNum = " + httpSession.getAttribute("authNum"));
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 5 * 60 * 1000);
>>>>>>> f81ed9f (비밀번호 인증)

            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);

            model.addAttribute("newPassword",newPassword);
            model.addAttribute("email", email);
//            model.addAttribute("message","새로운 비밀 번호는" + newPassword + "입니다.");
            System.out.println(newPassword);
        }
<<<<<<< HEAD
        else model.addAttribute("errorMessage","해당 이메일을 사용하시는 회원이 없습니다.");


        return "members/findPasswordResult";
=======
        else model.addAttribute("errorMessage","해당 이메일과 전화번호가 일치하는 회원이 없습니다. 다시 확인해주세요");
//        return "members/findPasswordResult";
        return "errorPage";
>>>>>>> f81ed9f (비밀번호 인증)
    }

    @PostMapping("/findPasswordResult")
    public String findPasswordResultAct(@RequestParam String email, @RequestParam String oldPassword,
                                        @RequestParam String newPassword,
                                        @RequestParam String newPasswordCheck,
                                        Model model) {
        Optional<Member> findemail = memberRepository.findByEmail(email);

        if (findemail.isPresent() == false) {
            return "redirect:/";
        }
        var user = findemail.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("errorMessage", "입력한 임시 비밀번호가 일치하지 않습니다.");
            model.addAttribute("email",email);
            return "members/findPasswordResult";
        }
        if (!newPassword.equals(newPasswordCheck)){
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("email",email);
            return "members/findPasswordResult";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(user);


        return "redirect:/";
    }

}
