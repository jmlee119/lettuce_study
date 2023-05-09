package lettuce.demo.Controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
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
import java.util.Timer;
import java.util.TimerTask;
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

        if(findnick.isPresent()){
            if(findnick.get().getPhone().equals(phone)){
                Member member = findnick.get();
                String youremail = member.getEmail();
                String yourname = member.getName();
                model.addAttribute("message",yourname+"의 이메일은 "+ youremail + "입니다.");
            } else {
                model.addAttribute("errorMessage","해당 전화번호를 사용하시는 회원이 없습니다.");
            }
        } else {
            model.addAttribute("errorMessage","해당 닉네임을 사용하시는 회원이 없습니다.");
        }

        return "members/findIdResult";
    }

    @GetMapping("/findPassword")
    public String findPassword(){return "members/findPassword";}


    @PostMapping("/findPassword")
    public String findPassword(@RequestParam("email") String email, Model model, HttpSession httpSession){
        Optional<Member> findemail = memberRepository.findByEmail(email);
        if(findemail.isPresent()){
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

                model.addAttribute("email", email);
                model.addAttribute("authNum",authNum);
                //            model.addAttribute("message","새로운 비밀 번호는" + newPassword + "입니다.");
                System.out.println(authNum);
                return "members/findPasswordResult";
            }
            else{
                model.addAttribute("errorMessage","회원가입은 하였으나 email인증을 하지 않았습니다. 다시 회원가입을 진행해 주세요");
                return "errorPage";
            }
        }
        else model.addAttribute("errorMessage","해당 이메일을 사용하시는 회원이 없습니다.");
//        return "members/findPasswordResult";
        return "errorPage";
    }
    
    @PostMapping("/findPasswordResult")
    public String findPasswordResult(@RequestParam("email") String email,
                                     @RequestParam("newPassword") String newPassword,
                                     @RequestParam("newPasswordCheck") String newPasswordCheck,
                                     @RequestParam("authNumber") String authNum,
                                     HttpSession httpSession,
                                     Model model) {
        String sessionAuthNum = (String) httpSession.getAttribute("authNum");

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            if (authNum.equals(sessionAuthNum)) {
                if (!passwordEncoder.matches(newPassword,optionalMember.get().getPassword())){
                    if (newPassword.equals(newPasswordCheck)) {
                        System.out.println("optionalMember.get().getPassword() = " + optionalMember.get().getPassword());
                        Member member = optionalMember.get();
                        member.setPassword(passwordEncoder.encode(newPassword));
                        memberRepository.save(member);
                        httpSession.removeAttribute("authNum");
                        model.addAttribute("successMessage", "비밀번호 변경이 완료되었습니다.");
                    } else {
                        model.addAttribute("errorMessage", "새로운 비밀번호와 확인용 비밀번호가 일치하지 않습니다.");
                        model.addAttribute("email",email);
                        return "members/findPasswordResult";
                    }
                }else {
                    model.addAttribute("errorMessage", "이전 비밀번호와 같습니다. 다른걸로 변경해 주세요.");
                    model.addAttribute("email",email);
                    return "members/findPasswordResult";
                }

            } else {
                model.addAttribute("errorMessage", "인증번호가 일치하지 않습니다.");
                model.addAttribute("email",email);
                return "members/findPasswordResult";
            }
        } else {
            model.addAttribute("errorMessage", "해당 이메일을 가진 회원이 없습니다.");
        }
        return "redirect:/";
    }
}
