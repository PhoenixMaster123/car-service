package springboot.bg.harisauto.job;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import springboot.bg.harisauto.email.EmailService;
import springboot.bg.harisauto.invoice.repository.InvoiceRepository;
import springboot.bg.harisauto.user.repository.UserRepository;

/**
 * DailyReportJob.java - Sends a daily admin report email at 8:00 AM.
 *
 * @author Kristian Popov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportJob {

  private final UserRepository userRepository;
  private final InvoiceRepository invoiceRepository;
  private final EmailService emailService;

  /**
   * Runs every day at 8:00 AM.
   * Collects stats and sends a summary email to the admin.
   */
  @Scheduled(cron = "0 0 8 * * *")
  public void sendDailyReport() {

    log.info("Starting daily admin report job...");

    try {
      LocalDateTime since24h = LocalDateTime.now().minusHours(24);

      long totalUsers = userRepository.count();
      long newUsers = userRepository.countByCreatedOnAfter(since24h);
      long totalInvoices = invoiceRepository.count();

      emailService.sendDailyReport(totalUsers, newUsers, totalInvoices);

      log.info("Daily report sent successfully. Total users: {}, New users (24h): {}, Total invoices: {}",
          totalUsers, newUsers, totalInvoices);

    } catch (Exception e) {
      log.error("Failed to send daily report: {}", e.getMessage(), e);
    }
  }
}
