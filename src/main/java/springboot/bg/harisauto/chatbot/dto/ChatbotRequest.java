package springboot.bg.harisauto.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatbotRequest.java - DTO class for AI request.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotRequest {
  private String prompt;
}
