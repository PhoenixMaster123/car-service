package springboot.bg.harisauto.common.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;

/**
 * AuthenticationMetaData.java - Represents the authentication metadata of a user.
 *
 * @author Kristian Popov
 */
@Data
@AllArgsConstructor
public class AuthenticationMetaData implements UserDetails, OAuth2User {

  private final UUID userId;

  private final String email;

  private final String password;

  private final UserRole role;

  private boolean isActive;

  private Map<String, Object> attributes;

  /** Constructor for users registered through the application. */
  public AuthenticationMetaData(UUID userId, String email, String password, UserRole role, boolean isActive) {
    this.userId = userId;
    this.email = email;
    this.password = password;
    this.role = role;
    this.isActive = isActive;
  }

  /** Constructor for users registered through the OAuth2 provider. */
  public AuthenticationMetaData(User user, Map<String, Object> attributes) {
    this.userId = user.getId();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.role = user.getRole();
    this.isActive = true;
    this.attributes = attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + this.role.name());

    return List.of(authority);
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.isActive;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.isActive;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.isActive;
  }

  @Override
  public boolean isEnabled() {
    return this.isActive;
  }

  @Override
  public String getName() {
    return this.email;
  }
}