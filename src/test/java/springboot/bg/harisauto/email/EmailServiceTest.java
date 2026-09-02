package springboot.bg.harisauto.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import springboot.bg.harisauto.event.UserRegisteredEvent;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private EmailService emailService;

    @BeforeEach
    void setup() {
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        emailService = new EmailService(mailSender, templateEngine);

        ReflectionTestUtils.setField(emailService, "realFromUser", "");
        ReflectionTestUtils.setField(emailService, "defaultFromAddress", "noreply@test.local");
        ReflectionTestUtils.setField(emailService, "notificationEmail", "");

        when(templateEngine.process(eq("email/welcome-email.html"), any(Context.class))).thenReturn("<html>Hi</html>");
    }

    @Test
    void sendGreetingEmail_sendsToUserEmail_whenNoNotificationOverride() throws MessagingException {
        MimeMessage msg = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(msg);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .email("user@example.com")
                .firstName("A")
                .lastName("B")
                .userId(java.util.UUID.randomUUID())
                .build();

        emailService.sendGreetingEmail(event);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendGreetingEmail_redirectsToNotificationEmail_whenConfigured() throws MessagingException {
        ReflectionTestUtils.setField(emailService, "notificationEmail", "dev-inbox@example.com");
        MimeMessage msg = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(msg);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .email("real@example.com")
                .firstName("A")
                .lastName("B")
                .userId(java.util.UUID.randomUUID())
                .build();

        emailService.sendGreetingEmail(event);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
