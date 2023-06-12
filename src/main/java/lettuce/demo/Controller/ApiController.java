package lettuce.demo.Controller;

import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
public class ApiController {

    @Autowired
    private final MemberRepository memberRepository;

    public ApiController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<String> searchMembersByNickname(@RequestParam("nickname") String nickname) {
        List<Member> members = memberRepository.findByNicknameContainingIgnoreCase(nickname);
        List<String> nicknames = members.stream()
                .map(Member::getNickname)
                .collect(Collectors.toList());
        return nicknames;
    }
}