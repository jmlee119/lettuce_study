package lettuce.demo.Service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lettuce.demo.Member.Member;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MailService {
    private JavaMailSender javaMailSender;
    private String authNum;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String createEmailCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            switch (random.nextInt(3)) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }
        String authNum = key.toString();
        return authNum;
    }

    public void createEmailForm(String email, String name, String authNum) throws MessagingException {

        String setFrom = "cslettucestudy@gmail.com";
        String setName = "양상추 Study";
        String subject = "양상추 Study 회원가입 인증 번호";

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/mail.html");
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("authNum", authNum);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, variables);
        String html = writer.toString();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(new InternetAddress(setFrom, setName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(html, true);
//        javaMailSender.send(message);
        System.out.println(authNum);
    }
}