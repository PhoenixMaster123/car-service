package springboot.bg.harisauto.common.config.scheduler;

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

  /**
   * Configures the scheduler to use a thread pool.
   */
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    scheduler.setThreadNamePrefix("scheduled-task-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(60);
    scheduler.initialize();
    taskRegistrar.setTaskScheduler(scheduler);
  }
}