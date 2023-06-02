package lettuce.demo.Repository;

import lettuce.demo.Entity.Member;
import lettuce.demo.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository <Post,Long> {
    List<Post> findAll();
    List<Post> findByMemberOrderByCreateDateDesc(Member member);
    Optional<Post> findById(Long Id);
    void deleteById(Long id);
    Page<Post> findAllByOrderByCreateDateDesc(Pageable pageable);
    Page<Post> findAllByLocationOrderByCreateDateDesc(String location, Pageable pageable);

    Page<Post> findAllByLocationStartingWithOrderByCreateDateDesc(String city, Pageable pageable);

    Page<Post> findByMember_NicknameContainingAndLocationOrderByCreateDateDesc(String nickname, String location, Pageable pageable);

    Page<Post> findByTitleContainingAndLocationOrderByCreateDateDesc(String nickname, String location, Pageable pageable);

    Page<Post> findByContentContainingAndLocationOrderByCreateDateDesc(String nickname, String location, Pageable pageable);

    Page<Post> findByMember_NicknameContainingAndLocationStartingWithOrderByCreateDateDesc(String nickname, String location, Pageable pageable);

    Page<Post> findByTitleContainingAndLocationStartingWithOrderByCreateDateDesc(String nickname, String location, Pageable pageable);

    Page<Post> findByContentContainingAndLocationStartingWithOrderByCreateDateDesc(String nickname, String location, Pageable pageable);
}
