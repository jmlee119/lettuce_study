package lettuce.demo.Controller;

import jakarta.persistence.EntityNotFoundException;
import lettuce.demo.Member.Member;
import lettuce.demo.Post.Post;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostController(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/create")
    public String createGetPost(@RequestParam Long memberId, Model model) {
        model.addAttribute("memberId", memberId);
        System.out.println("createGetPost");
        return "createPost";
    }

//    @PostMapping("")
//    public String create(@RequestParam(required = true) Long memberId, @RequestParam String title, @RequestParam String content) {
//        Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
//        Post post = new Post();
//        post.setTitle(title);
//        post.setContent(content);
//        post.setMember(member);
//        postRepository.save(post);
//        return "redirect:/posts/list";
//    }
    @PostMapping("/create")
    public String createPost(@RequestParam Long memberId, @RequestParam String title, @RequestParam String content) {
        System.out.println("createPostPost");
//        Member member = memberRepository.findByEmail(email);
        Optional<Member> member = memberRepository.findById(memberId);
        if (!member.isPresent()) {
            // 해당 이메일을 가진 회원이 존재하지 않을 경우 에러 처리
            return "error";
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setMember(member.get());

        postRepository.save(post);

        return "redirect:/post/list";
    }
}