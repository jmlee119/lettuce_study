package lettuce.demo.Repository;

import lettuce.demo.Member.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends CrudRepository<Member,Long> {

    public List<Member> findByName(String name);
    public Optional<Member> findByEmail(String email);
    public Optional<Member> findByNickname(String nickname);

<<<<<<< HEAD
=======
    public Optional<Member> findByPhone(String phone);
>>>>>>> f81ed9f (비밀번호 인증)

}
