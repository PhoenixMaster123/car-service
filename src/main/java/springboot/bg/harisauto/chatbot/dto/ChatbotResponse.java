package springboot.bg.harisauto.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatbotResponse.java - DTO class for AI response.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotResponse {
  private String response;
}
