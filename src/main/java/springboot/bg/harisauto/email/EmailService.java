package springboot.bg.harisauto.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
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
}
