package springboot.bg.harisauto.common.config.async;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * AsyncConfig.java - Enables {@code @Async} execution and defines the executor it uses.
 *
 * <p>Without {@link EnableAsync} every {@code @Async} method runs inline on the calling
 * thread. That matters most for the registration welcome email: it is a synchronous
 * {@code @EventListener} that declares a checked {@code MessagingException}, so an SMTP
 * failure would propagate into the registration transaction and roll the new user back.</p>
 *
 * @author Kristian Popov
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  /** Bounded executor for {@code @Async} work, so a slow mail server cannot exhaust threads. */
  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("async-task-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    return executor;
  }
}
