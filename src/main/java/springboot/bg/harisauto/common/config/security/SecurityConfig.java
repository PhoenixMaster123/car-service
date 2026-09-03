package springboot.bg.harisauto.common.config.security;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  /**
   * Signs remember-me tokens. Set {@code app.remember-me.key} (env {@code REMEMBER_ME_KEY})
   * in every real deployment. When left blank a random key is generated at startup, which
   * is safe but invalidates existing remember-me cookies on each restart.
   */
  @Value("${app.remember-me.key:}")
  private String rememberMeKey;

  /** Security filter chain for all requests. **/
  @Bean
  @Order(4)
  public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService oauth2Service,
      CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
    http.authorizeHttpRequests(matcher -> matcher
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .requestMatchers(
            "/common/**",
            "/component/**",
            "/auth/**",
            "/public/**",
            "/uploads/**",
            "/fonts/**",
            "/favicon.ico",
            "/error"
        ).permitAll()
        // "/login" is listed explicitly: formLogin().permitAll() only covers the bare
        // path and its error/logout variants, so /login?lang=de was redirected away and
        // the language switcher did not work on the sign-in page.
        .requestMatchers("/register", "/", "/login").permitAll()
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
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
            .logoutSuccessUrl("/")
        )
        .rememberMe(rememberMe -> rememberMe
            .rememberMeParameter("remember")
            .key(resolveRememberMeKey())
            .tokenValiditySeconds(1209600) // 14 days
        );
    return http.build();
  }

  /**
   * Returns the configured remember-me key, or a freshly generated one if none is set.
   *
   * @return The key used to sign remember-me tokens.
   */
  private String resolveRememberMeKey() {
    if (rememberMeKey == null || rememberMeKey.isBlank()) {
      log.warn("app.remember-me.key is not set; generating a random key. "
          + "Remember-me cookies will not survive a restart.");
      return UUID.randomUUID().toString();
    }
    return rememberMeKey;
  }

  /** Password encoder bean using BCrypt hashing algorithm. **/
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}