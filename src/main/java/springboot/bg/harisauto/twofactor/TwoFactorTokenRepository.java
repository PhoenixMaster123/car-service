package springboot.bg.harisauto.twofactor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TwoFactorTokenRepository.java - Persistence for one-time sign-in codes.
 *
 * @author Kristian Popov
 */
@Repository
public interface TwoFactorTokenRepository extends JpaRepository<TwoFactorToken, UUID> {

  /** The newest code issued to a user, used or not. */
  Optional<TwoFactorToken> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

  /** Removes every code for a user, so issuing a new one invalidates the old. */
  void deleteByUserId(UUID userId);

  /**
   * Removes codes that expired before the given moment.
   *
   * @param cutoff Codes expiring before this are deleted.
   * @return How many rows were removed.
   */
  long deleteByExpiresAtBefore(LocalDateTime cutoff);
}
