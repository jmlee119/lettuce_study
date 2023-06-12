package lettuce.demo.Repository;

import lettuce.demo.Entity.Mail;
import lettuce.demo.Entity.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MailRepository extends CrudRepository<Mail,Long> {
    List<Mail> findByReceiverOrderBySendDateDesc(Member receiver);

    List<Mail> findBySenderOrderBySendDateDesc(Member sender);

    List<Mail> findByReceiverOrSenderOrderBySendDateDesc(Member receiver, Member sender);
    long countByReceiverAndIsread(Member receiver, boolean read);
}
