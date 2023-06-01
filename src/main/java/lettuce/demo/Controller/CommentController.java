package lettuce.demo.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lettuce.demo.Entity.Comment;
import lettuce.demo.Entity.Member;
import lettuce.demo.Entity.Reply;
import lettuce.demo.Repository.CommentRepository;
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
@RequestMapping("/comment")
public class CommentController {
    @Autowired private ReplyRepository replyRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private MemberRepository memberRepository;

    @PostMapping("/createcomment")
    @PreAuthorize("isAuthenticated()")
    public String createComment(@RequestParam("replyId") Long replyId, @RequestParam("content") String content,@RequestParam("extends") Boolean extend, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Optional<Reply> findReply = replyRepository.findById(replyId);

        if (findReply.isPresent()) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreateDate(new Date());
            comment.setReply(findReply.get());
            comment.setMember(findMember.get());
            commentRepository.save(comment);
        }
        boolean isExtend = extend != null && extend;
        String extendsParam = request.getParameter("extends");
        if (extendsParam != null && !extendsParam.isEmpty() || isExtend) {
            return "redirect:/posts/detail/" + findReply.get().getPost().getId() + "?extends=" + extendsParam;
        } else {
            return "redirect:/posts/detail/" + findReply.get().getPost().getId();
        }
    }

    @GetMapping("/delete/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable("commentId") Long commentId, @RequestParam("postId") Long postId, Model model) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("memberId", findmember.get().getId());
        model.addAttribute("nickname", findmember.get().getNickname());
        if (commentOptional.isPresent() && commentOptional.get().getMember().getNickname().equals(findmember.get().getNickname()) ) {
            Comment comment = commentOptional.get();
            commentRepository.delete(comment);
            return "redirect:/posts/detail/" + postId;
        }
        else{
            model.addAttribute("errorMessage", "댓글을 작성하신 사용자와 다릅니다. 삭제 불가능합니다");
            return "errorPage";
        }

    }
}
