package springboot.bg.harisauto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springboot.bg.harisauto.chatbot.service.GeminiService;
import springboot.bg.harisauto.email.EmailService;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private GeminiService geminiService;

    @Test
    void contextLoads() {
    }

}