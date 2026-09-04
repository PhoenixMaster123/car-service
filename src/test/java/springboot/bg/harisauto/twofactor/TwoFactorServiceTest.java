package springboot.bg.harisauto.twofactor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springboot.bg.harisauto.email.EmailService;
import springboot.bg.harisauto.user.model.User;

class TwoFactorServiceTest {

    private TwoFactorTokenRepository repository;
    private EmailService emailService;
    private TwoFactorProperties properties;
    private PasswordEncoder passwordEncoder;
    private TwoFactorService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        repository = mock(TwoFactorTokenRepository.class);
        emailService = mock(EmailService.class);
        passwordEncoder = new BCryptPasswordEncoder();
        properties = new TwoFactorProperties();
        service = new TwoFactorService(repository, passwordEncoder, emailService, properties);
    }

    private User user() {
        User user = new User();
        user.setId(userId);
        user.setEmail("driver@example.com");
        return user;
    }

    private TwoFactorToken tokenFor(String code) {
        return TwoFactorToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .codeHash(passwordEncoder.encode(code))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .attempts(0)
                .consumed(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void issueCode_storesOnlyAHashAndEmailsTheCode() {
        service.issueCode(user());

        ArgumentCaptor<TwoFactorToken> saved = ArgumentCaptor.forClass(TwoFactorToken.class);
        verify(repository).save(saved.capture());
        ArgumentCaptor<String> emailed = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendTwoFactorCode(eq("driver@example.com"), emailed.capture(), anyInt());

        String code = emailed.getValue();
        assertThat(code).hasSize(6).containsOnlyDigits();
        // The stored value must not be the code itself.
        assertThat(saved.getValue().getCodeHash()).isNotEqualTo(code);
        assertThat(passwordEncoder.matches(code, saved.getValue().getCodeHash())).isTrue();
    }

    @Test
    void issueCode_removesAnyEarlierCodeFirst() {
        service.issueCode(user());
        verify(repository).deleteByUserId(userId);
    }

    @Test
    void verify_withTheCorrectCode_succeedsAndConsumesIt() {
        TwoFactorToken token = tokenFor("123456");
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(token));

        assertThat(service.verify(userId, "123456"))
                .isEqualTo(TwoFactorService.VerificationResult.SUCCESS);
        assertThat(token.isConsumed()).isTrue();
    }

    @Test
    void verify_rejectsAConsumedCode_soItCannotBeReplayed() {
        TwoFactorToken token = tokenFor("123456");
        token.setConsumed(true);
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(token));

        assertThat(service.verify(userId, "123456"))
                .isEqualTo(TwoFactorService.VerificationResult.NO_CODE);
    }

    @Test
    void verify_rejectsAnExpiredCodeAndDiscardsIt() {
        TwoFactorToken token = tokenFor("123456");
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(token));

        assertThat(service.verify(userId, "123456"))
                .isEqualTo(TwoFactorService.VerificationResult.EXPIRED);
        verify(repository).delete(token);
    }

    @Test
    void verify_withAWrongCode_countsTheAttempt() {
        TwoFactorToken token = tokenFor("123456");
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(token));

        assertThat(service.verify(userId, "000000"))
                .isEqualTo(TwoFactorService.VerificationResult.INVALID);
        assertThat(token.getAttempts()).isEqualTo(1);
        verify(repository, never()).delete(any(TwoFactorToken.class));
    }

    @Test
    void verify_discardsTheCodeOnceTheAttemptLimitIsReached() {
        TwoFactorToken token = tokenFor("123456");
        token.setAttempts(properties.getMaxAttempts() - 1);
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(token));

        assertThat(service.verify(userId, "000000"))
                .isEqualTo(TwoFactorService.VerificationResult.TOO_MANY_ATTEMPTS);
        verify(repository, times(1)).delete(token);
    }

    @Test
    void verify_withNoIssuedCode_fails() {
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.empty());

        assertThat(service.verify(userId, "123456"))
                .isEqualTo(TwoFactorService.VerificationResult.NO_CODE);
    }

    @Test
    void verify_withANullCode_isRejectedRatherThanThrowing() {
        TwoFactorToken token = tokenFor("123456");
        when(repository.findFirstByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Optional.of(token));

        assertThat(service.verify(userId, null))
                .isEqualTo(TwoFactorService.VerificationResult.INVALID);
    }
}
