package lettuce.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
//@Controller
//class TestController {
//	@Autowired MemberRepository repo;
//
//	@GetMapping("/")
//	@ResponseBody
//
//	public List<Member> getAll() {
//		List<Member> members = new ArrayList<>();
//		repo.findAll().forEach(members::add);
//
//		return members;
//	}
//
//	@GetMapping("/name")
//	@ResponseBody
//	public List<Member> findByName(String name) {
//		List<Member> members = repo.findByName(name);
//
//		return members;
//	}
//
//	@GetMapping("/new")
//	@ResponseBody
//	public boolean addMember(String name, String nickname) {
//		var member = new Member();
//		member.setId(0L);
//		member.setName(name);
//		member.setNickname(nickname);
//
//		try {
//			repo.save(member);
//		}catch (Exception e) {
//			return false;
//		}
//
//		return true;
//	}
//}
