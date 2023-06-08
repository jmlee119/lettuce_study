package lettuce.demo.Controller;

import jakarta.validation.Valid;
import lettuce.demo.Entity.Mail;
import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.MailRepository;
import lettuce.demo.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/send")
    public String sendMail(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("nickname", findMember.get().getNickname());
        model.addAttribute("memberId", findMember.get().getId());
        Mail mail = new Mail();
        Member sender = findMember.get();
        model.addAttribute("sender", sender);
        return "Mail/send";
    }
    @PostMapping("/send")
    public String send(@Valid Mail mail, @RequestParam("receiverNickname") String receiverNickname,
                       @RequestParam("title") String title, @RequestParam("content") String content) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Optional<Member> receiver = memberRepository.findByNickname(receiverNickname);
        mail.setReceiver(receiver.orElseThrow(() -> new IllegalArgumentException("Invalid receiver")));
        mail.setSender(findMember.get());
        mail.setTitle(title);
        mail.setContent(content);
        mail.setSendDate(new Date());
        mailRepository.save(mail);
        return "redirect:/mail/lists/" + findMember.get().getNickname();
    }

    @GetMapping("/lists/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public String list(@PathVariable String nickname, Model model,@RequestParam("who") String who) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> Me = memberRepository.findByEmail(authentication.getName());
        if (!Me.get().getNickname().equals(nickname)) {
            model.addAttribute("errorMessage" , "본인의 메일함만 볼 수 있습니다");
            return "errorPage";
        }

        Optional<Member> findMember = memberRepository.findByNickname(nickname);
       // model.addAttribute("nickname", findMember.get().getNickname());
        List<Mail> listmail;
        if (who.equalsIgnoreCase("Receiver")) {
            listmail = mailRepository.findByReceiver(findMember.get());
        } else if (who.equalsIgnoreCase("Sender")) {
            listmail = mailRepository.findBySender(findMember.get());
        }else{
            listmail = mailRepository.findByReceiverOrSender(findMember.get(),findMember.get());
        }
        model.addAttribute("mail", listmail);
        model.addAttribute("nickname", Me.get().getNickname());
        return "Mail/list";
    }


    @GetMapping("/detail/{mailId}")
    @PreAuthorize("isAuthenticated()")
    public String mailDetail(@PathVariable Long mailId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Optional<Mail> findMail = mailRepository.findById(mailId);
        model.addAttribute("mail", findMail.get());

        return "Mail/detail";

    }

}
