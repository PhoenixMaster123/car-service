package springboot.bg.harisauto.common.config.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * SchedulerConfig.java - Configuration for scheduled tasks.
 * Provides a thread pool for running scheduled jobs concurrently.
 *
 * @author Kristian Popov
 */
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

  private final ThreadPoolTaskScheduler taskScheduler;

  /** Constructor. */
  public SchedulerConfig(ThreadPoolTaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  /**
   * The scheduler used for {@code @Scheduled} tasks.
   *
   * <p>Declared as a bean so the container owns its lifecycle. Built by hand and
   * merely handed to the registrar, it was never destroyed on shutdown, so
   * {@code waitForTasksToCompleteOnShutdown} and {@code awaitTerminationSeconds}
   * never took effect and every devtools restart leaked another pool.</p>
   *
   * @return The managed scheduler.
   */
  @Bean
  public static ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    scheduler.setThreadNamePrefix("scheduled-task-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(60);
    return scheduler;
  }

  /**
   * Points scheduled tasks at the managed scheduler.
   */
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setTaskScheduler(taskScheduler);
  }
}