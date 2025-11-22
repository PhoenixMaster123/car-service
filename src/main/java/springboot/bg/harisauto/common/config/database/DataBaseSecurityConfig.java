package springboot.bg.harisauto.common.config.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * DataBaseSecurityConfig.java - Configuration class for database security settings.
 *
 * @author Kristian Popov
 */
@Configuration
@Profile("dev")
public class DataBaseSecurityConfig {

  /** H2 Console Security Filter Chain. Only for testing purposes. **/
  @Bean
  @Order(1)
  public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
    http
      .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

    return http.build();
  }
}