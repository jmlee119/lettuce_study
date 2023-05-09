package lettuce.demo.Repository;

import lettuce.demo.Member.Member;
import lettuce.demo.Post.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository <Post,Long> {
//    Member findByEmail(String email);
    List<Post> findByMemberEmail(String email);
    List<Post> findAll();

    List<Post> findAllByOrderByCreateDateDesc();

    List<Post> findByMemberOrderByCreateDateDesc(Member member);

    Optional<Post> findById(Long Id);

}
