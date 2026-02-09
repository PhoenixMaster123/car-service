package springboot.bg.harisauto.common.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * SwaggerSecurityConfig.java - Security configuration for Swagger UI access in non-production environments.
 *
 * @author Kristian Popov
 */
@Profile("!prod")
@Configuration
public class SwaggerSecurityConfig {

  /** Configures security settings for Swagger UI access. **/
  @Bean
  @Order(2)
  public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http, PasswordEncoder encoder) throws Exception {

    InMemoryUserDetailsManager inMemoryManager = new InMemoryUserDetailsManager();
    inMemoryManager.createUser(User.withUsername("user")
        .password(encoder.encode("password"))
        .roles("USER")
        .build());

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(inMemoryManager);
    provider.setPasswordEncoder(encoder);

    http.securityMatcher(
          "/swagger-ui.html",
          "/swagger-ui/index.html",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/swagger-resources/**"
    )
    .csrf(AbstractHttpConfigurer::disable)
    .authorizeHttpRequests((authorize) -> authorize
        .requestMatchers(
            "/v3/api-docs/**",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/swagger-resources/**"
        ).permitAll()
        .anyRequest().authenticated()
    )
    .httpBasic((httpBasicCustomizer) -> httpBasicCustomizer.realmName("car-service"))
    .authenticationProvider(provider)
    .sessionManagement((sessionManagement) ->
      sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ).addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);

    return http.build();
  }
}
