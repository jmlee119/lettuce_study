package lettuce.demo.Member;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
