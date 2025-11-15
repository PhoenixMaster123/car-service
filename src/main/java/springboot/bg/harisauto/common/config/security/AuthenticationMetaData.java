package springboot.bg.harisauto.common.config.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springboot.bg.harisauto.user.model.UserRole;

/**
 * AuthenticationMetaData.java - Represents the authentication metadata of a user.
 *
 * @author Kristian Popov
 */
@Data
@AllArgsConstructor
public class AuthenticationMetaData implements UserDetails {

  private final UUID userId;

  private final String email;

  private final String password;

  private final UserRole role;

  private boolean isActive;

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
}