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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
        http.authorizeRequests().anyRequest().permitAll();
        http.formLogin().loginPage("/member/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((req, res, auth) -> {
                    Optional<Member> member = memberRepository.findByEmail(auth.getName());
                    if (!member.get().getVerified()) {
                        throw new BadCredentialsException("The account is not verified.");
                    }
                    res.sendRedirect("/");
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
