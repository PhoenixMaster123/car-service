package springboot.bg.harisauto.chatbot.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import springboot.bg.harisauto.chatbot.dto.ChatbotRequest;
import springboot.bg.harisauto.chatbot.service.GeminiService;

@WebMvcTest(GeminiController.class)
class GeminiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private GeminiService geminiService;

  @Test
  @DisplayName("POST /api/gemini/ask returns AI response JSON")
  @WithMockUser
  void askGeminiApi_returnsResponse() throws Exception {
    when(geminiService.getGeminiResponse("Hello AI"))
        .thenReturn("Hi driver! How can I help you?");

    ChatbotRequest req = ChatbotRequest.builder().prompt("Hello AI").build();

    mockMvc.perform(post("/api/gemini/ask")
            .with(csrf())
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(content().json("{" +
            "\"response\":\"Hi driver! How can I help you?\"}"));
  }
}
