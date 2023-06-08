package lettuce.demo.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 100)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

    @ManyToOne
    @JoinColumn(name ="sender_id" , referencedColumnName = "id",nullable = false)
    private Member sender;

    @ManyToOne
    @JoinColumn(name ="receiver_id" , referencedColumnName = "id",nullable = false)
    private Member receiver;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getSender() {
        return sender;
    }

    public void setSender(Member sender) {
        this.sender = sender;
    }


    public Member getReceiver() {
        return receiver;
    }

    public void setReceiver(Member receiver) {
        this.receiver = receiver;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
}
