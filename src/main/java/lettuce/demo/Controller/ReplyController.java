package lettuce.demo.Controller;

import lettuce.demo.Member.Member;
import lettuce.demo.Post.Post;
import lettuce.demo.Reply.Reply;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import lettuce.demo.Repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String createReply(@RequestParam("postId") Long postId, @RequestParam("content") String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        Optional<Post> findPost = postRepository.findById(postId);

        if (findPost.isPresent()) {
            Reply reply = new Reply();
            reply.setContent(content);
            reply.setCreateDate(new Date());
            reply.setModifyDate(new Date());
            reply.setPost(findPost.get());
            reply.setMember(findmember.get());

            // 답글 저장
            replyRepository.save(reply);
        }

        return "redirect:/posts/detail/" + postId;
    }
}
