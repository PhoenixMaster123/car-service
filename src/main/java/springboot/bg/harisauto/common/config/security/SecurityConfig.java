package springboot.bg.harisauto.common.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig.java - Security configuration for the application.
 *
 * @author Kristian Popov
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /** Security filter chain for all requests. **/
  @Bean
  @Order(4)
  public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService oauth2Service,
      CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
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
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
    )
        .formLogin(form -> form
            .loginPage("/login")
            .usernameParameter("email")
            .defaultSuccessUrl("/home", true)
            .failureUrl("/login?error")
            .permitAll()
    )
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .failureUrl("/login?error")
            .userInfoEndpoint(userInfo -> userInfo.userService(oauth2Service))
            .defaultSuccessUrl("/home", true)
        )
        .exceptionHandling(exception -> exception
            .accessDeniedHandler(accessDeniedHandler)
        )
        .logout(logout -> logout
            .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout"))
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
