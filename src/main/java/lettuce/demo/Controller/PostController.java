package lettuce.demo.Controller;


import jakarta.servlet.http.HttpServletRequest;
import lettuce.demo.Entity.Member;
import lettuce.demo.Entity.Post;
import lettuce.demo.Entity.Reply;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import lettuce.demo.Repository.ReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    private final ReplyRepository replyRepository;

    public PostController(PostRepository postRepository, MemberRepository memberRepository, ReplyRepository replyRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.replyRepository = replyRepository;
    }


    @GetMapping("/lists")
    @PreAuthorize("isAuthenticated()")
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable,
                       @RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "criteria", required = false) String criteria) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        String memberLocation = findMember.get().getLocation();
        if(memberLocation == null){
            model.addAttribute("errorMessage", "현재 위치인증을 받지 않아 글 보기 기능을 사용할 수 없습니다. 위치인증을 먼저 받아주세요!!");
            return "errorPage";
        }
        String[] memberLocationSplit = memberLocation.split(" ");
        String city = memberLocationSplit[0];
        Page<Post> postPage;
        if (search != null && !search.isEmpty()) {
            if (criteria.equals("nickname")) {
                if (findMember.get().isAdmin()) {
                    postPage = postRepository.findByMember_NicknameContainingOrderByCreateDateDesc(search, pageable);
                } else {
                    postPage = postRepository.findByMember_NicknameContainingAndLocationOrderByCreateDateDesc(search, memberLocation, pageable);
                }
            } else if (criteria.equals("title")) {
                if (findMember.get().isAdmin()) {
                    postPage = postRepository.findByTitleContainingOrderByCreateDateDesc(search, pageable);
                } else {
                    postPage = postRepository.findByTitleContainingAndLocationOrderByCreateDateDesc(search, memberLocation, pageable);
                }
            } else if (criteria.equals("content")) {
                if (findMember.get().isAdmin()) {
                    postPage = postRepository.findByContentContainingOrderByCreateDateDesc(search, pageable);
                } else {
                    postPage = postRepository.findByContentContainingAndLocationOrderByCreateDateDesc(search, memberLocation, pageable);
                }
            } else {
                if (findMember.get().isAdmin()) {
                    postPage = postRepository.findAllByOrderByCreateDateDesc(pageable);
                } else {
                    postPage = postRepository.findAllByLocationOrderByCreateDateDesc(memberLocation, pageable);
                }
            }
        } else if (findMember.get().isAdmin()) {
            postPage = postRepository.findAllByOrderByCreateDateDesc(pageable);
        } else {
            postPage = postRepository.findAllByLocationOrderByCreateDateDesc(memberLocation, pageable);
        }
        model.addAttribute("nickname", findMember.get().getNickname());
        model.addAttribute("memberId", findMember.get().getId());
        model.addAttribute("postPage", postPage);
        model.addAttribute("member", findMember);
        model.addAttribute("extends",city);
        return "Post/postList";
    }

    @GetMapping("/extends")
    @PreAuthorize("isAuthenticated()")
    public String extendlist(Model model, @PageableDefault(size = 10) Pageable pageable,
                             @RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "criteria", required = false) String criteria){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        String memberLocation = findMember.get().getLocation();
        if (memberLocation == null) {
            model.addAttribute("errorMessage", "현재 위치인증을 받지 않아 글 보기 기능을 사용할 수 없습니다. 위치인증을 먼저 받아주세요!!");
            return "errorPage";
        }

        String[] memberLocationSplit = memberLocation.split(" ");
        String city = memberLocationSplit[0];
        Page<Post> postPage;
        if (search != null && !search.isEmpty()) {
            if (criteria.equals("nickname")) {
                postPage = postRepository.findByMember_NicknameContainingAndLocationStartingWithOrderByCreateDateDesc(search, city, pageable);
            } else if (criteria.equals("title")) {
                postPage = postRepository.findByTitleContainingAndLocationStartingWithOrderByCreateDateDesc(search, city, pageable);
            } else if (criteria.equals("content")) {
                postPage = postRepository.findByContentContainingAndLocationStartingWithOrderByCreateDateDesc(search, city, pageable);
            } else {
                // Handle invalid criteria value
                postPage = postRepository.findAllByLocationStartingWithOrderByCreateDateDesc(city, pageable);
            }
        } else {
            postPage = postRepository.findAllByLocationStartingWithOrderByCreateDateDesc(city, pageable);
        }
        model.addAttribute("nickname", findMember.get().getNickname());
        model.addAttribute("memberId", findMember.get().getId());
        model.addAttribute("postPage", postPage);
        model.addAttribute("member", findMember);
        model.addAttribute("extends",city);
        return "Post/extendslist";
    }

    @GetMapping("/create/{memberId}")
    public String postCreate(@PathVariable Long memberId, Model model) {
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.get().getLocation() == null) {
            model.addAttribute("errorMessage", "현재 위치인증을 받지 않아 글 생성이 불가능 합니다. 위치인증을 먼저 받아주세요!!");
            return "errorPage";
        }
        model.addAttribute("memberId", memberId);
        model.addAttribute("nickname", member.get().getNickname());
        return "Post/createPost";
    }

    @PostMapping("/create")
    public String save(@Validated @ModelAttribute Post post, Model model, Principal principal) {
        String username = principal.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            post.setMember(member);
            post.setCreateDate(new Date());
            post.setLocation(optionalMember.get().getLocation());
            postRepository.save(post);
            Long postId = post.getId();
            return "redirect:/posts/detail/" + postId;
        } else {
            return "error-page";
        }

    }

    @GetMapping("/detail/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String myPostDetail(@PathVariable Long postId, Model model,@RequestParam(value = "extends", required = false) Boolean extend, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Optional<Post> findPost = postRepository.findById(postId);
        if (findMember.get().getLocation() == null){
            model.addAttribute("errorMessage","아직 동네인증 전으로 글을 볼 수 없습니다.");
            return "errorPage";
        }
        if (findPost.isPresent()) {
            String referer = request.getHeader("Referer");
            boolean isFromExtend = referer != null && (referer.contains("/posts/extends") || (extend != null && extend));


            if(!findMember.get().isAdmin() && !isFromExtend){
                if(!findMember.get().getLocation().equals(findPost.get().getLocation())
                        && !findMember.get().getId().equals(findPost.get().getMember().getId())){
                    model.addAttribute("errorMessage", "글을 쓴 위치와 현재 사용자의 위치가 달라서 글을 볼 수 없습니다.");
                    return "errorPage";
                }
            }
            model.addAttribute("post", findPost.get());
            model.addAttribute("memberId", findMember.get().getId());
            model.addAttribute("nickname", findMember.get().getNickname());
            List<Reply> replies = replyRepository.findByPost(findPost.get());
            model.addAttribute("replies", replies);
            model.addAttribute("extends",isFromExtend);
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
            model.addAttribute("memberId", findMember.get().getId());
            model.addAttribute("nickname", findMember.get().getNickname());
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
            post.setModifyDate(new Date());
            postRepository.save(post);
            return "redirect:/posts/detail/" + postId;
        } else{
            model.addAttribute("errorMessage","404 not found");
            return "errorPage";
        }
    }
}
