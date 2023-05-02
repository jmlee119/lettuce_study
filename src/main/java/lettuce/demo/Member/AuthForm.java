package lettuce.demo.Member;

import jakarta.validation.constraints.NotBlank;

public class AuthForm {

    @NotBlank
    private String authNum;

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }
}
