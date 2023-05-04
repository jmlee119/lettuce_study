package lettuce.demo.Controller;

import lettuce.demo.Member.Member;
import lettuce.demo.Post.Post;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private final MemberRepository memberRepository;
    @Autowired
    private final PostRepository postRepository;
    public MyPageController(MemberRepository memberRepository,PostRepository postRepository){
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/myinfo")
    @PreAuthorize("isAuthenticated()")
    public String Mypage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("email",findmember.get().getEmail());
        model.addAttribute("name",findmember.get().getName());
        model.addAttribute("nickname",findmember.get().getNickname());
        model.addAttribute("phone",findmember.get().getPhone());
        return "mypage/mypage";
    }

    @GetMapping("/mylist")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        List<Post> myPosts = postRepository.findByMemberOrderByCreateDateDesc(findMember.get());
        model.addAttribute("myPosts", myPosts);
        return "mypage/mylist";
    }
}
