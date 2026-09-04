package springboot.bg.harisauto.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import springboot.bg.harisauto.twofactor.TwoFactorTokenRepository;

class TwoFactorTokenCleanupJobTest {

    private TwoFactorTokenRepository repository;
    private TwoFactorTokenCleanupJob job;

    @BeforeEach
    void setup() {
        repository = mock(TwoFactorTokenRepository.class);
        job = new TwoFactorTokenCleanupJob(repository);
    }

    @Test
    void removeExpiredTokens_deletesEverythingAlreadyExpired() {
        when(repository.deleteByExpiresAtBefore(any(LocalDateTime.class))).thenReturn(3L);

        LocalDateTime before = LocalDateTime.now();
        job.removeExpiredTokens();
        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<LocalDateTime> cutoff = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(repository).deleteByExpiresAtBefore(cutoff.capture());

        // The cutoff is "now", so unexpired codes are never touched.
        assertThat(cutoff.getValue()).isBetween(before, after);
    }

    @Test
    void removeExpiredTokens_withNothingToRemove_stillCompletes() {
        when(repository.deleteByExpiresAtBefore(any(LocalDateTime.class))).thenReturn(0L);

        job.removeExpiredTokens();

        verify(repository).deleteByExpiresAtBefore(any(LocalDateTime.class));
    }
}
