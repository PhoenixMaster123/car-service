package springboot.bg.harisauto.common.config.monitoring;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ActuatorSecurityConfig.java - Security for the management endpoints.
 *
 * <p>The endpoints are served on {@code management.server.port} (9090), separately from the
 * public site on 8080, and are open on that port so Prometheus can scrape without carrying
 * credentials. That is only safe because the port is meant to stay internal - it must be
 * firewalled and never published. If it has to be reachable from an untrusted network,
 * replace the {@code permitAll} below with authentication.</p>
 *
 * @author Kristian Popov
 */
@Configuration
public class ActuatorSecurityConfig {

  /** Filter chain for the management port. */
  @Bean
  @Order(0)
  public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher(EndpointRequest.toAnyEndpoint())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
