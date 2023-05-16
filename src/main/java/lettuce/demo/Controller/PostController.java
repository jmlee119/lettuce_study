package lettuce.demo.Controller;


import lettuce.demo.Member.Member;
import lettuce.demo.Post.Post;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.annotation.Id;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.util.List;
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


    @GetMapping("/lists")
    @PreAuthorize("isAuthenticated()")
    public String list(Model model, @PageableDefault(size = 10)Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByOrderByCreateDateDesc(pageable);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("nickname", findMember.get().getNickname());
        model.addAttribute("memberId", findMember.get().getId());
        model.addAttribute("postPage", postPage); // Pass the Page object to the view
        model.addAttribute("member", findMember);
        return "Post/postList";
    }

    @GetMapping("/create/{memberId}")
    public String postCreate(@PathVariable Long memberId, Model model) {
        Optional<Member> member = memberRepository.findById(memberId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("nickname", member.get().getNickname());
        return "Post/createPost";
    }

    @PostMapping("/create")
    public String save(@Validated @ModelAttribute Post post, Model model, Principal principal) {
//        model.addAttribute("title", post.getTitle());
//        model.addAttribute("content", post.getContent());
        String username = principal.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
//        Member member = (Member) memberRepository.findByName(username);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            post.setMember(member);
            postRepository.save(post);
            Long postId = post.getId();
            return "redirect:/posts/detail/" + postId;
        } else {
            return "error-page";
        }

    }

    @GetMapping("/detail/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String myPostDetail(@PathVariable Long postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isPresent()) {
            model.addAttribute("post", findPost.get());
            model.addAttribute("memberId", findmember.get().getId());
            model.addAttribute("nickname", findmember.get().getNickname());
            return "Post/detail";
        } else {
            return "redirect:/mypage/mylist";
        }
    }

    @GetMapping("/delete/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable Long postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Post> findId = postRepository.findById(postId);
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if (findId.get().getMember().getNickname().equals(findMember.get().getNickname())) {
            postRepository.deleteById(postId);
            return "redirect:/posts/lists";
        } else {
            model.addAttribute("errorMessage", "본인의 글만 삭제 가능합니다.");
            return "errorPage";
        }
    }

    @GetMapping("/update/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Post> findpost = postRepository.findById(postId);
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if (findpost.get().getMember().getNickname().equals(findMember.get().getNickname())) {
            model.addAttribute("post", findpost);
            model.addAttribute("title", findpost.get().getTitle());
            model.addAttribute("content", findpost.get().getContent());
            return "Post/updatePost";
        } else {
            model.addAttribute("errorMessage", "본인의 글만 수정 가능합니다");
            return "errorPage";
        }
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@RequestParam Long postId,
                             @RequestParam String title,
                             @RequestParam String content, Model model) {
        Optional<Post> findpost = postRepository.findById(postId);
        if (findpost.isPresent()) {
            Post post = findpost.get();
            post.setTitle(title);
            post.setContent(content);
            postRepository.save(post);
            return "redirect:/posts/detail/" + postId;
        } else{
            model.addAttribute("errorMessage","404 not found");
            return "errorPage";
        }


    }
}
