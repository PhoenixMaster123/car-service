package springboot.bg.harisauto.common.config.swagger;

import org.springframework.beans.factory.annotation.Value;
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
 * SwaggerSecurityConfig.java - Security configuration for Swagger UI access.
 *
 * <p>Gated on the "dev" profile rather than on "!prod": there is no "prod" profile in
 * this repository, so a "!prod" guard was active in the default profile - which is what
 * production runs. Opting in is the safe direction.</p>
 *
 * @author Kristian Popov
 */
@Profile("dev")
@Configuration
public class SwaggerSecurityConfig {

  @Value("${springdoc.auth.username:docs}")
  private String docsUsername;

  @Value("${springdoc.auth.password:docs}")
  private String docsPassword;

  /** Configures security settings for Swagger UI access. **/
  @Bean
  @Order(2)
  public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http, PasswordEncoder encoder,
      AuthenticationEntryPoint authenticationEntryPoint) throws Exception {

    InMemoryUserDetailsManager inMemoryManager = new InMemoryUserDetailsManager();
    inMemoryManager.createUser(User.withUsername(docsUsername)
        .password(encoder.encode(docsPassword))
        .roles("USER")
        .build());

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(inMemoryManager);
    provider.setPasswordEncoder(encoder);

    http.securityMatcher(
          "/swagger-ui.html",
          "/swagger-ui/index.html",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/swagger-resources/**"
    )
    .csrf(AbstractHttpConfigurer::disable)
    // Every matched path requires the HTTP Basic credentials below. Previously these
    // were all permitAll(), which made the credentials and the authenticated() rule
    // unreachable and left the API description open to anonymous callers.
    .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
    // Use the existing AuthenticationEntryPoint bean so an unauthenticated request gets
    // a 401 with a WWW-Authenticate header, not a redirect to the HTML login page.
    .httpBasic(httpBasicCustomizer -> httpBasicCustomizer
        .authenticationEntryPoint(authenticationEntryPoint))
    .exceptionHandling(exception -> exception
        .authenticationEntryPoint(authenticationEntryPoint))
    .authenticationProvider(provider)
    .sessionManagement((sessionManagement) ->
      sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ).addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);

    return http.build();
  }
}