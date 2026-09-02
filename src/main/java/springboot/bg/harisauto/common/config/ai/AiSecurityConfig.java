package springboot.bg.harisauto.common.config.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * AISecurityConfig.java - Security configuration for AI-related endpoints.
 *
 * @author Kristian Popov
 */
@Configuration
@EnableWebSecurity
public class AiSecurityConfig {

  /** Security filter chain for AI-related endpoints. **/
  @Bean
  @Order(3)
  public SecurityFilterChain aiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
        .csrf(csrf -> csrf
          .ignoringRequestMatchers("/api/**")
        )
        .authorizeHttpRequests(auth -> auth
          // /error must be reachable, otherwise a failure inside the API is dispatched
          // to /error, falls through to the form-login chain and returns a 302 to the
          // login page instead of an error response.
          .requestMatchers("/error").permitAll()
          // The chatbot spends the server's Gemini quota on every call, so it is not
          // open to anonymous callers.
          .anyRequest().authenticated()
        )
        .exceptionHandling(exception -> exception
          .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        );
    return http.build();
  }
}
