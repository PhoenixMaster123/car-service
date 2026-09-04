package springboot.bg.harisauto.twofactor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.bg.harisauto.email.EmailService;
import springboot.bg.harisauto.user.model.User;

/**
 * TwoFactorService.java - Issues and verifies the one-time codes used at sign-in.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class TwoFactorService {

  private static final SecureRandom RANDOM = new SecureRandom();

  private final TwoFactorTokenRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final TwoFactorProperties properties;

  /** Constructor. */
  public TwoFactorService(TwoFactorTokenRepository repository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          TwoFactorProperties properties) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.properties = properties;
  }

  /**
   * Issues a fresh code for a user and emails it.
   *
   * <p>Any earlier code is deleted first, so only the most recent one can ever be used.</p>
   *
   * @param user The user signing in.
   */
  @Transactional
  public void issueCode(User user) {

    repository.deleteByUserId(user.getId());

    // 6 digits, uniformly distributed over 000000-999999.
    String code = String.format("%06d", RANDOM.nextInt(1_000_000));

    repository.save(TwoFactorToken.builder()
        .userId(user.getId())
        .codeHash(passwordEncoder.encode(code))
        .expiresAt(LocalDateTime.now().plusMinutes(properties.getExpiryMinutes()))
        .attempts(0)
        .consumed(false)
        .createdAt(LocalDateTime.now())
        .build());

    // The code is passed to the mailer and then goes out of scope; it is never logged.
    emailService.sendTwoFactorCode(user.getEmail(), code, properties.getExpiryMinutes());

    log.info("Issued a two-factor code for user {}", user.getId());
  }

  /**
   * Checks a submitted code.
   *
   * <p>A wrong code counts against the attempt limit; reaching the limit discards the code
   * entirely, so guessing forces the user to start sign-in again rather than continue
   * against the same secret.</p>
   *
   * @param userId The user being verified.
   * @param submittedCode The code entered.
   * @return The outcome of the check.
   */
  @Transactional
  public VerificationResult verify(UUID userId, String submittedCode) {

    Optional<TwoFactorToken> found = repository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    if (found.isEmpty()) {
      return VerificationResult.NO_CODE;
    }

    TwoFactorToken token = found.get();

    if (token.isConsumed()) {
      return VerificationResult.NO_CODE;
    }

    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      repository.delete(token);
      return VerificationResult.EXPIRED;
    }

    if (submittedCode == null || !passwordEncoder.matches(submittedCode, token.getCodeHash())) {
      token.setAttempts(token.getAttempts() + 1);
      if (token.getAttempts() >= properties.getMaxAttempts()) {
        repository.delete(token);
        log.warn("Two-factor code for user {} discarded after {} failed attempts",
            userId, token.getAttempts());
        return VerificationResult.TOO_MANY_ATTEMPTS;
      }
      repository.save(token);
      return VerificationResult.INVALID;
    }

    token.setConsumed(true);
    repository.save(token);
    log.info("Two-factor code accepted for user {}", userId);
    return VerificationResult.SUCCESS;
  }

  /** Outcome of checking a submitted code. */
  public enum VerificationResult {
    /** The code matched and has now been consumed. */
    SUCCESS,
    /** The code did not match; further attempts remain. */
    INVALID,
    /** The code was past its expiry and has been discarded. */
    EXPIRED,
    /** The attempt limit was reached and the code has been discarded. */
    TOO_MANY_ATTEMPTS,
    /** No usable code exists for this user. */
    NO_CODE
  }
}
