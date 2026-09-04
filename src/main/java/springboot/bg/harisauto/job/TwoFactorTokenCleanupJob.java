package springboot.bg.harisauto.job;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springboot.bg.harisauto.twofactor.TwoFactorTokenRepository;

/**
 * TwoFactorTokenCleanupJob.java - Removes expired sign-in codes.
 *
 * <p>A code is deleted when it is used, when somebody tries it after expiry, or when the
 * attempt limit is reached. None of those cover an abandoned sign-in, so without this the
 * table keeps a row - and a hash of authentication material - for every code that was never
 * entered.</p>
 *
 * @author Kristian Popov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TwoFactorTokenCleanupJob {

  private final TwoFactorTokenRepository repository;

  /** Runs every day at 4:00 AM. */
  @Scheduled(cron = "0 0 4 * * *")
  @Transactional
  public void removeExpiredTokens() {

    long removed = repository.deleteByExpiresAtBefore(LocalDateTime.now());

    if (removed > 0) {
      log.info("Removed {} expired two-factor tokens", removed);
    }
  }
}
