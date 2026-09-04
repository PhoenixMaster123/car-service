package springboot.bg.harisauto.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import springboot.bg.harisauto.event.UserRegisteredEvent;

/**
 * Feedback.java - Entity class for Feedback.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class EmailService {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username:}")
  private String realFromUser;

  @Value("${notification.email}")
  private String defaultFromAddress;

  @Value("${notification.email:}")
  private String notificationEmail;

  @Autowired
  public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
    this.javaMailSender = javaMailSender;
    this.templateEngine = templateEngine;
  }

  /**
   * Sends a plain text email to the user upon registration.
   *
   * @param event The event containing the user's email address.
   * @throws MessagingException If the email could not be sent.
   */
  @Async
  @EventListener
  public void sendGreetingEmail(UserRegisteredEvent event) throws MessagingException {
    Context context = new Context();
    String htmlBody = templateEngine.process("email/welcome-email.html", context);

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    String from = realFromUser.isEmpty() ? defaultFromAddress : realFromUser;
    helper.setFrom(from);
    helper.setSubject("Welcome to Car Service!");
    helper.setText(htmlBody, true);

    String recipient;
    if (notificationEmail != null && !notificationEmail.isEmpty()) {
      recipient = notificationEmail;
      log.warn("DEV MODE: Redirecting email for {} to {}", event.getEmail(), recipient);
    } else {
      recipient = event.getEmail();
    }
    helper.setTo(recipient);

    log.info("Sending email to: {} (From: {})", recipient, from);
    javaMailSender.send(message);
  }

  /**
   * Sends the generated invoice to the customer with the PDF attached.
   *
   * <p>Delivery failures are logged rather than propagated, so that a failing mail
   * server never rolls back an invoice that was already persisted.</p>
   *
   * @param to            The customer's email address.
   * @param pdfBytes      The rendered invoice PDF.
   * @param invoiceNumber The invoice number, used for the subject and attachment name.
   */
  @Async
  public void sendInvoiceEmail(String to, byte[] pdfBytes, String invoiceNumber) {
    try {
      Context context = new Context();
      context.setVariable("invoiceNumber", invoiceNumber);
      String htmlBody = templateEngine.process("email/invoice-email.html", context);

      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      String from = realFromUser.isEmpty() ? defaultFromAddress : realFromUser;
      helper.setFrom(from);
      helper.setSubject("Your invoice " + invoiceNumber);
      helper.setText(htmlBody, true);
      helper.addAttachment(invoiceNumber + ".pdf", new ByteArrayResource(pdfBytes),
          "application/pdf");

      String recipient;
      if (notificationEmail != null && !notificationEmail.isEmpty()) {
        recipient = notificationEmail;
        log.warn("DEV MODE: Redirecting invoice email for {} to {}", to, recipient);
      } else {
        recipient = to;
      }
      helper.setTo(recipient);

      log.info("Sending invoice {} to: {} (From: {})", invoiceNumber, recipient, from);
      javaMailSender.send(message);
    } catch (MessagingException e) {
      log.error("Failed to send invoice email {} to {}: {}", invoiceNumber, to, e.getMessage(), e);
    }
  }

  /**
   * Sends the daily summary report to the configured administrator address.
   *
   * <p>Delivery failures are logged rather than propagated, so that a failing mail
   * server never aborts the scheduled job.</p>
   *
   * @param totalUsers    Total number of registered users.
   * @param newUsers      Users registered in the last 24 hours.
   * @param totalInvoices Total number of invoices issued.
   */
  @Async
  public void sendDailyReport(long totalUsers, long newUsers, long totalInvoices) {
    try {
      Context context = new Context();
      context.setVariable("reportDate", LocalDate.now().toString());
      context.setVariable("totalUsers", totalUsers);
      context.setVariable("newUsers", newUsers);
      context.setVariable("totalInvoices", totalInvoices);
      String htmlBody = templateEngine.process("email/daily-report-email.html", context);

      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      String from = realFromUser.isEmpty() ? defaultFromAddress : realFromUser;
      String recipient = (notificationEmail != null && !notificationEmail.isEmpty())
          ? notificationEmail : defaultFromAddress;

      helper.setFrom(from);
      helper.setTo(recipient);
      helper.setSubject("Daily report - " + LocalDate.now());
      helper.setText(htmlBody, true);

      log.info("Sending daily report to: {} (From: {})", recipient, from);
      javaMailSender.send(message);
    } catch (MessagingException e) {
      log.error("Failed to send daily report: {}", e.getMessage(), e);
    }
  }

  /**
   * Sends a one-time sign-in code.
   *
   * <p>Unlike the other mail here, delivery failures are <em>not</em> swallowed. If the
   * code never arrives the user cannot sign in, so the caller has to know: it refuses the
   * sign-in rather than leaving them stuck on a code that was never sent. This also runs
   * synchronously for the same reason.</p>
   *
   * @param to The user's email address.
   * @param code The six-digit code.
   * @param expiryMinutes How long the code stays valid.
   */
  public void sendTwoFactorCode(String to, String code, int expiryMinutes) {
    try {
      Context context = new Context();
      context.setVariable("code", code);
      context.setVariable("expiryMinutes", expiryMinutes);
      String htmlBody = templateEngine.process("email/two-factor-email.html", context);

      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      String from = realFromUser.isEmpty() ? defaultFromAddress : realFromUser;
      helper.setFrom(from);
      helper.setSubject("Your sign-in code");
      helper.setText(htmlBody, true);

      String recipient;
      if (notificationEmail != null && !notificationEmail.isEmpty()) {
        recipient = notificationEmail;
        log.warn("DEV MODE: Redirecting sign-in code for {} to {}", to, recipient);
      } else {
        recipient = to;
      }
      helper.setTo(recipient);

      // The code itself is deliberately never logged.
      log.info("Sending a sign-in code to {}", recipient);
      javaMailSender.send(message);
    } catch (MessagingException e) {
      throw new IllegalStateException("Could not send the sign-in code", e);
    }
  }
}
