package springboot.bg.harisauto.common.config.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
          .requestMatchers("/api/gemini/**").permitAll()
          .anyRequest().authenticated()
        );
    return http.build();
  }
}
