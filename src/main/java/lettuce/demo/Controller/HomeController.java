package lettuce.demo.Controller;

import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    private MemberRepository memberRepository;
    public HomeController(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nickname = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("NICKNAME_"))
                .findFirst()
                .map(a -> a.replace("NICKNAME_", ""))
                .orElse("");
        model.addAttribute("nickname", nickname);
        Optional<Member> member = memberRepository.findByNickname(nickname);
        Long memberId = member.map(Member::getId).orElse(null);
        if (memberId == null) {
            member = memberRepository.findByEmail(authentication.getName());
            memberId = member.map(Member::getId).orElse(null);
        }
        model.addAttribute("memberId", memberId);
        if (member.isPresent()) {
            model.addAttribute("nickname", member.get().getNickname());
        }
        return "index";
    }
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
