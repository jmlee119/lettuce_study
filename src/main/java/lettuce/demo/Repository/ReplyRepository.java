package lettuce.demo.Repository;

import lettuce.demo.Post.Post;
import lettuce.demo.Reply.Reply;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends CrudRepository<Reply,Long> {
    List<Reply> findByPost(Post post);
}
