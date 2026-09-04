package springboot.bg.harisauto.twofactor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TwoFactorToken.java - A one-time code emailed to a user during sign-in.
 *
 * <p>The code itself is never stored. Only a BCrypt hash is kept, so a reader of the
 * database cannot complete somebody's sign-in.</p>
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "two_factor_tokens")
public class TwoFactorToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  /** BCrypt hash of the six-digit code. */
  @Column(nullable = false)
  private String codeHash;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  /** Failed verification attempts against this code. */
  @Column(nullable = false)
  private int attempts;

  /** Set once the code has been used, so a code can never be replayed. */
  @Column(nullable = false)
  private boolean consumed;

  @Column(nullable = false)
  private LocalDateTime createdAt;
}
