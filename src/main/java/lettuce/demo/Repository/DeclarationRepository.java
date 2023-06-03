package lettuce.demo.Repository;

import lettuce.demo.Entity.Declaration;
import lettuce.demo.Entity.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeclarationRepository extends CrudRepository<Declaration,Long> {
    List<Declaration> findAllByOrderByCreateDateDesc();
}
