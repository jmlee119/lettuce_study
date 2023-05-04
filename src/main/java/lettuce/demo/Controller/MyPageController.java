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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("member",findmember);
        return "myPage/mypage";
    }

//    @GetMapping("/myinfo/{nickname}")
//    @PreAuthorize("isAuthenticated()")
//    public String Mypage(@RequestParam("nickname") String nickname, Model model){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
//        model.addAttribute("member",findmember);
//        return "myPage/mypage";
//    }

    @GetMapping("/mylist")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if (findMember.isPresent()) {
            List<Post> myPosts = postRepository.findByMemberOrderByCreateDateDesc(findMember.get());
            model.addAttribute("myPosts", myPosts);
            model.addAttribute("nickname",findMember.get().getNickname());
            return "myPage/mylist";
        } else {
            return "redirect:/";
        }
//        return "myPage/mylist";
    }

//    @GetMapping("/detail/{postId}")
//    @PreAuthorize("isAuthenticated()")
//    public String myPostDetail(@PathVariable Long postId, Model model) {
//        Optional<Post> findPost = postRepository.findById(postId);
//        if (findPost.isPresent()) {
//            model.addAttribute("post", findPost.get());
//            return "Post/detail";
//        } else {
//            return "redirect:/mypage/mylist";
//        }
//    }
}
