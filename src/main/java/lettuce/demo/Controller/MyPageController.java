package lettuce.demo.Controller;

import lettuce.demo.Entity.Member;
import lettuce.demo.Entity.Post;
import lettuce.demo.Repository.MemberRepository;
import lettuce.demo.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class MyPageController {

    private final MemberRepository memberRepository;
    @Autowired
    private final PostRepository postRepository;
    public MyPageController(MemberRepository memberRepository,PostRepository postRepository){
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/myinfo/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public String Mypage(@PathVariable String nickname, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findmember = memberRepository.findByEmail(authentication.getName());
        if (nickname.equals(findmember.get().getNickname())) {
            model.addAttribute("member", findmember.get());
            model.addAttribute("memberId",findmember.get().getId());
            model.addAttribute("nickname",findmember.get().getNickname());
            model.addAttribute("nickname_profile", findmember.get().getNickname());
            model.addAttribute("real",findmember.get());
            return "myPage/mypage";
        } else {
            Optional<Member> anothermember = memberRepository.findByNickname(nickname);
            if (anothermember.isPresent()) { // 값이 있는지 먼저 확인
                model.addAttribute("member", anothermember.get());
                model.addAttribute("memberId",findmember.get().getId());
                model.addAttribute("nickname",findmember.get().getNickname());
                model.addAttribute("nickname_profile", anothermember.get().getNickname());
                model.addAttribute("real",findmember.get());
                return "myPage/mypage";
            } else { // 값이 없을 경우 예외 처리 또는 적절한 대응
                model.addAttribute("errorMessage", "해당 회원을 찾을 수 없습니다.");
                return "errorPage"; // 예외 처리 페이지로 이동하거나, 다른 적절한 대응을 취할 수 있습니다.
            }
        }
    }


    @GetMapping("/mylist/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(@PathVariable String nickname, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if ((nickname.equals(findMember.get().getNickname()))){
            if (findMember.isPresent()) {
                List<Post> myPosts = postRepository.findByMemberOrderByCreateDateDesc(findMember.get());
                model.addAttribute("myPosts", myPosts);
                model.addAttribute("memberId",findMember.get().getId());
                model.addAttribute("nickname",findMember.get().getNickname());
                return "myPage/mylist";
            } else {
                return "redirect:/";
            }
        }else{
            Optional<Member> anotherMember = memberRepository.findByNickname(nickname);
            if (anotherMember.isPresent()) {
                List<Post> anotherPosts = postRepository.findByMemberOrderByCreateDateDesc(anotherMember.get());
                model.addAttribute("myPosts", anotherPosts);
                model.addAttribute("nickname", findMember.get().getNickname());
                return "myPage/mylist";
            } else {
                return "redirect:/";
            }
        }
    }

    @GetMapping("/edit/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@PathVariable String nickname, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        if (nickname.equals(findMember.get().getNickname())){
            model.addAttribute("member", findMember);
            model.addAttribute("nickname",findMember.get().getNickname());
            model.addAttribute("memberId",findMember.get().getId());
            return "myPage/edit";
        }
        else{
            model.addAttribute("errorMessage","로그인한 유저와 Myprofile 수정대상이 다릅니다.");
            return "errorPage";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@RequestParam("Id") Long Id,
                                @RequestParam(value = "nickname", defaultValue = "", required = true) String nickname,
                                @RequestParam("name") String name,
                                @RequestParam("phone") String phone,
                                @RequestParam("github") String github,
                                @RequestParam("instagram") String instagram,
                                @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                Model model) {
        Optional<Member> findmember = memberRepository.findById(Id);
        if (findmember.isPresent()) { // 값이 있는지 먼저 확인
            Member member = findmember.get();
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    byte[] imageData = imageFile.getBytes();
                    member.setImage(imageData);
                    member.setImageType(imageFile.getContentType());
                } catch (IOException e) {
                    model.addAttribute("errorMessage", "이미지 업로드 중 오류가 발생했습니다.");
                    return "errorPage";
                }
            } else {
                member.setImage(null);
            }
            if (!StringUtils.isEmpty(nickname) && !member.getNickname().equals(nickname) && memberRepository.findByNickname(nickname).isPresent()) {
                model.addAttribute("errorMessage", "이미 사용 중인 닉네임입니다.");
                model.addAttribute("member", member);
                model.addAttribute("nickname",nickname);
                model.addAttribute("nickname_profile", nickname);
//                return "redirect:/profile/edit/" + nickname;
                return "errorPage";
            }
            member.setNickname(nickname);
            member.setName(name);
            member.setPhone(phone);
            member.setGithub(github);
            member.setInstagram(instagram);
            memberRepository.save(member);
            model.addAttribute("member", member);
            model.addAttribute("nickname", nickname);
            model.addAttribute("nickname_profile", member.getNickname());
//            return "myPage/mypage";
            return "redirect:/profile/myinfo/" + nickname;
        } else {
            model.addAttribute("errorMessage", "해당 회원을 찾을 수 없습니다.");
            return "errorPage";
        }
    }

    @GetMapping(value = "/get-image/{memberId}", produces = { MediaType.IMAGE_JPEG_VALUE, "image/webp" })
    public ResponseEntity<byte[]> getImage(@PathVariable Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isPresent() && member.get().getImage() != null) {
            byte[] imageBytes = member.get().getImage();
            HttpHeaders headers = new HttpHeaders();
            if (MediaType.IMAGE_JPEG_VALUE.equals(member.get().getImageType())) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else if ("image/webp".equals(member.get().getImageType())) {
                headers.setContentType(MediaType.parseMediaType("image/webp"));
            }
            headers.setContentLength(imageBytes.length);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
