package lettuce.demo.Repository;

import lettuce.demo.Entity.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends CrudRepository<Member,Long> {

    public Optional<Member> findByEmail(String email);

    public Optional<Member> findByNickname(String nickname);

    List<Member> findByNicknameContainingIgnoreCase(String nickname);
}
