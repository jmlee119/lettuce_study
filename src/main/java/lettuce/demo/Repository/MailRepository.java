package lettuce.demo.Repository;

import lettuce.demo.Entity.Mail;
import lettuce.demo.Entity.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MailRepository extends CrudRepository<Mail,Long> {
    List<Mail> findByReceiver(Member member);

    List<Mail> findBySender(Member member);

    List<Mail> findByReceiverOrSender(Member receiver, Member sender);
}
