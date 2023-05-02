package lettuce.demo.Controller;

import jakarta.mail.MessagingException;
import lettuce.demo.Member.Member;
import lettuce.demo.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/member")
public class LoginController {

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
//        System.out.println("findnick.get().getPhone() = " + findnick.get().getPhone());
//        System.out.println("findnick.get().getPhone() = " + findnick.get().getNickname());
//        System.out.println(findnick.get().getPhone() == phone);
        if(findnick.isPresent()){
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

//    @PostMapping("/findPassword")
//    public String findPw(@RequestParam("email") String email, Model model) throws MessagingException{
//        Optional<Member> findemail = memberRepository.findByEmail(email);
//        if(findemail.isPresent()){
//            String newPassword =
//        }
//    }
}
