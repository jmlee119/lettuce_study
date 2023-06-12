package lettuce.demo.Config;

import lettuce.demo.Entity.Member;
import lettuce.demo.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig{

    @Autowired
    private MemberRepository memberRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests().requestMatchers("/profile/**").authenticated();
        http.authorizeRequests().requestMatchers("/posts/**").authenticated();
        http.authorizeRequests().requestMatchers("/reply/**").authenticated();
        http.authorizeRequests().requestMatchers("/comment/**").authenticated();
        http.authorizeRequests().requestMatchers("/admin/**").authenticated();
        http.authorizeRequests().requestMatchers("/mail/**").authenticated();
        http.authorizeRequests().anyRequest().permitAll();
        http.formLogin().loginPage("/member/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((req, res, auth) -> {
                    Optional<Member> member = memberRepository.findByEmail(auth.getName());
                    if (!member.get().getVerified()) {
                        String errorMessage = "회원가입 진행 후 메일인증을 하지 않았습니다. 인증을 진행해 주세요";
                        res.sendRedirect("/error-page?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
                    } else if (!member.get().getEnable()) {
                        String errorMessage = "신고누적으로 인해 계정이 정지당했습니다. admin@naver.com으로 문의 주세요";
                        res.sendRedirect("/error-page?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
                    } else{
                        res.sendRedirect("/");
                    }
                })
                .permitAll();
        http
                .logout()
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
