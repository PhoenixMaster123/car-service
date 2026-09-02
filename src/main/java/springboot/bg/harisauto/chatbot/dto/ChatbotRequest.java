package springboot.bg.harisauto.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

  /** Capped so a single request cannot run up an unbounded Gemini bill. */
  @NotBlank(message = "Prompt must not be empty")
  @Size(max = 2000, message = "Prompt must be at most 2000 characters")
  private String prompt;
}
