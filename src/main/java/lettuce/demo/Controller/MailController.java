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

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    @PreAuthorize("isAuthenticated()")
    public String sendMail(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("nickname", findMember.get().getNickname());
        model.addAttribute("memberId", findMember.get().getId());
        Member sender = findMember.get();
        model.addAttribute("sender", sender);
        return "Mail/send";
    }
    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public String send(@Valid Mail mail, @RequestParam("receiverNickname") String receiverNickname,
                       @RequestParam("title") String title, @RequestParam("content") String content,Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Optional<Member> receiver = memberRepository.findByNickname(receiverNickname);
        if (!receiver.isPresent()) {
            model.addAttribute("errorMessage", "받는사람이 존재하지 않는 유저입니다. 다시 입력해 주세요");
            return "errorPage";
        }
        mail.setReceiver(receiver.orElseThrow(() -> new IllegalArgumentException("Invalid receiver")));
        mail.setSender(findMember.get());
        mail.setTitle(title);
        mail.setContent(content);
        mail.setSendDate(new Date());
        mail.setIsread(false);
        mailRepository.save(mail);
        return "redirect:/mail/lists/" + URLEncoder.encode( findMember.get().getNickname(), StandardCharsets.UTF_8) + "?who=All";
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
        List<Mail> listmail;
        if (who.equalsIgnoreCase("Receiver")) {
            listmail = mailRepository.findByReceiverOrderBySendDateDesc(findMember.get());
        } else if (who.equalsIgnoreCase("Sender")) {
            listmail = mailRepository.findBySenderOrderBySendDateDesc(findMember.get());
        }else{
            listmail = mailRepository.findByReceiverOrSenderOrderBySendDateDesc(findMember.get(),findMember.get());
        }
        model.addAttribute("mail", listmail);
        model.addAttribute("nickname", Me.get().getNickname());
        long mailCount = mailRepository.countByReceiverAndIsread(findMember.get(),false);
        model.addAttribute("mailCount", mailCount);
        return "Mail/list";
    }


    @GetMapping("/detail/{mailId}")
    @PreAuthorize("isAuthenticated()")
    public String mailDetail(@PathVariable Long mailId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Optional<Mail> findMail = mailRepository.findById(mailId);
        if(findMember.get().getNickname().equals(findMail.get().getReceiver().getNickname()) || findMember.get().getNickname().equals(findMail.get().getSender().getNickname()) ){
            model.addAttribute("mail", findMail.get());
            if (findMember.get().getNickname().equals(findMail.get().getReceiver().getNickname())){
                findMail.get().setIsread(true);
                mailRepository.save(findMail.get());
            }
    }else{
        model.addAttribute("errorMessage","로그인 한 유저와 메일 유저가 달라 상세 메일을 볼 수 없습니다.");
        return "errorPage";
    }
        return "Mail/detail";
    }
}
