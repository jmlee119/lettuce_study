package lettuce.demo.Repository;

import lettuce.demo.Member.Member;
import lettuce.demo.Post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository <Post,Long> {
    List<Post> findAll();
    List<Post> findByMemberOrderByCreateDateDesc(Member member);
    Optional<Post> findById(Long Id);
    void deleteById(Long id);
    Page<Post> findAllByOrderByCreateDateDesc(Pageable pageable);
}
