package springboot.bg.harisauto.common.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SecurityConfig.java - Security configuration for the application.
 *
 * @author Kristian Popov
 */
@Configuration
public class SecurityConfig {

  /** Security filter chain for all requests. **/
  @Bean
  @Order(4)
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(matcher -> matcher
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .requestMatchers("/register", "/").permitAll()
        .requestMatchers(
            "/services",
            "/about",
            "/careers",
            "/news",
            "/locations",
            "/careers",
            "/checkout")
            .permitAll()
            .anyRequest().authenticated()
    )
        .formLogin(form -> form
            .loginPage("/login")
            .usernameParameter("email")
            .defaultSuccessUrl("/home", true)
            .failureUrl("/login?error")
            .permitAll()
    )
        .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
            .logoutSuccessUrl("/")
        )
        .rememberMe(rememberMe -> rememberMe
            .rememberMeParameter("remember")
            .key("Tp2bb7csFj1C1gyA8CHbQStyLtqlKgP5")
            .tokenValiditySeconds(1209600) // 14 days
        );
    return http.build();
  }

  /** Password encoder bean using BCrypt hashing algorithm. **/
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}