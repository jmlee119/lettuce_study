package lettuce.demo.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lettuce.demo.Entity.Member;
import lettuce.demo.Entity.Post;
import lettuce.demo.Entity.Reply;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import lettuce.demo.Repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("reply")
public class ReplyController {

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/createreply")
    @PreAuthorize("isAuthenticated()")
    public String createReply(@RequestParam("postId") Long postId, @RequestParam("content") String content, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        Optional<Post> findPost = postRepository.findById(postId);

        if (findPost.isPresent()) {
            Reply reply = new Reply();
            reply.setContent(content);
            reply.setCreateDate(new Date());
            reply.setPost(findPost.get());
            reply.setMember(findmember.get());
            replyRepository.save(reply);
        }

//        return "redirect:/posts/detail/" + postId;
        String extendsParam = request.getParameter("/posts/extends");
        if (extendsParam != null && !extendsParam.isEmpty()) {
            return "redirect:/posts/detail/" + postId + "?extends=" + extendsParam;
        } else {
            return "redirect:/posts/detail/" + postId;
        }
    }

    @GetMapping("/delete/{replyId}")
    @PreAuthorize("isAuthenticated()")
    public String deleteReply(@PathVariable Long replyId, @RequestParam("postId") Long postId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        Optional<Reply> findReply = replyRepository.findById(replyId);
        model.addAttribute("memberId", findmember.get().getId());
        model.addAttribute("nickname", findmember.get().getNickname());
        if(findReply.isPresent() && findReply.get().getMember().getNickname().equals(findmember.get().getNickname())) {
            replyRepository.delete(findReply.get());
            return "redirect:/posts/detail/" + postId;
        }
        else{
            model.addAttribute("errorMessage", "답글을 작성하신 사용자와 다릅니다. 삭제 불가능합니다");
            return "errorPage";
        }
    }
}
