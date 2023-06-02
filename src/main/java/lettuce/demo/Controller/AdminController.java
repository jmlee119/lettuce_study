package lettuce.demo.Controller;

import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired private MemberRepository memberRepository;
    @GetMapping("/main")
    @PreAuthorize("isAuthenticated()")
    public String addForm(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if (findMember.get().isAdmin()){
            model.addAttribute("nickname", findMember.get().getNickname());
            model.addAttribute("memberId", findMember.get().getId());
            return "Admin/MainAdminPage";
        }
        else{
            model.addAttribute("errorMessage","관리자 권한이 없습니다.");
            return "errorPage";
        }
    }

//    @GetMapping("declaration")
//    @PreAuthorize("isAuthenticated()")
//    public String Declaration(Model model){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
//    }
}
