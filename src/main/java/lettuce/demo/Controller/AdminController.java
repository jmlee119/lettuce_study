package lettuce.demo.Controller;

import jakarta.validation.Valid;
import lettuce.demo.Entity.Declaration;
import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.DeclarationRepository;
import lettuce.demo.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired private MemberRepository memberRepository;
    @Autowired private DeclarationRepository declarationRepository;
    @GetMapping("/main")
    @PreAuthorize("isAuthenticated()")
    public String addForm(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if (findMember.get().isAdmin()){
            List<Declaration> declarations = declarationRepository.findAllByOrderByCreateDateDesc();
            model.addAttribute("nickname", findMember.get().getNickname());
            model.addAttribute("memberId", findMember.get().getId());
            model.addAttribute("declaration",declarations);
            return "Admin/MainAdminPage";
        }
        else{
            model.addAttribute("errorMessage","관리자 권한이 없습니다.");
            return "errorPage";
        }
    }

    @GetMapping("/declaration")
    @PreAuthorize("isAuthenticated()")
    public String Declaration(Model model, @RequestParam("reported") Long reportedId, @RequestParam(value = "content", required = false) String content){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("nickname",findMember.get().getNickname());
        model.addAttribute("memberId",findMember.get().getId());
        Declaration declaration = new Declaration();
        Member reporter = findMember.get();
        Member reported = memberRepository.findById(reportedId).orElse(null);
        if(findMember.get().getId() == reported.getId()){
            model.addAttribute("errorMessage","신고대상과 신고자가 같습니다.");
            return "errorPage";
        }
        declaration.setReporter(reporter);
        declaration.setReported(reported);
        declaration.setReport_content(content);
        model.addAttribute("declaration",declaration);
        return "Admin/declaration";
    }
    @PostMapping("/declaration")
    @PreAuthorize("isAuthenticated()")
    public String submitDeclaration(@ModelAttribute("declaration") @Valid Declaration declaration,
                                    BindingResult result, @RequestParam("reporter") Long reporterId,
                                    @RequestParam("reported") Long reportedId,
                                    @RequestParam("setReport_content") String setReport_content, @RequestParam("content") String content) {
        if (result.hasErrors()) {
            return "Admin/declaration";
        }

        Member reporter = memberRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("없는 신고자 " + reporterId));

        Member reported = memberRepository.findById(reportedId)
                .orElseThrow(() -> new IllegalArgumentException("없는 신고대상 " + reportedId));

        declaration.setReporter(reporter);
        declaration.setReported(reported);
        declaration.setReport_content(setReport_content);
        declaration.setContent(content);
        declaration.setCreateDate(new Date());
        declarationRepository.save(declaration);

        return "redirect:/profile/myinfo/" + declaration.getReporter().getNickname();
    }
    
    @GetMapping("/detaildeclaration/{id}")
    @PreAuthorize("isAuthenticated()")
    public String detaildeclaration(@PathVariable("id") Long id ,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        model.addAttribute("nickname",findMember.get().getNickname());
        model.addAttribute("memberId",findMember.get().getId());
        Optional<Declaration> optionalDeclaration = declarationRepository.findById(id);
        if(optionalDeclaration.isPresent()){
            Declaration declaration = optionalDeclaration.get();
            model.addAttribute("declaration",declaration);
        }
        else{
            model.addAttribute("errorMessage","글을 찾지 못했습니다");
            return "errorPage";
        }
        return "Admin/detaildeclaration";
    }

    @PostMapping("/disable-account/{nickname}")
    public String disableAccount(@PathVariable String nickname,Model model) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Invalid nickname: " + nickname));
        if (!member.getEnable()) {
            model.addAttribute("errorMessage","이미 계정이 비활성화 되있습니다.");
            return "errorPage";
        }
        member.setEnable(false);
        memberRepository.save(member);

        return "redirect:/admin/main";
    }

    @PostMapping("/enable-account/{nickname}")
    public String ensableAccount(@PathVariable String nickname,Model model) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Invalid nickname: " + nickname));
        if (member.getEnable()) {
            model.addAttribute("errorMessage","이미 계정이 활성화 되있습니다.");
            return "errorPage";
        }
        member.setEnable(true);
        memberRepository.save(member);

        return "redirect:/admin/main";
    }
}
