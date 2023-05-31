package lettuce.demo.Repository;

import lettuce.demo.Entity.Post;
import lettuce.demo.Entity.Reply;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReplyRepository extends CrudRepository<Reply,Long> {
    List<Reply> findByPost(Post post);
}
