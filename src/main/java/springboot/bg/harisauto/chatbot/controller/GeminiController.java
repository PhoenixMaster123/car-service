package springboot.bg.harisauto.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.bg.harisauto.chatbot.dto.ChatbotRequest;
import springboot.bg.harisauto.chatbot.dto.ChatbotResponse;
import springboot.bg.harisauto.chatbot.service.GeminiService;

/**
 * GeminiController.java - Controller for handling Gemini AI API requests.
 *
 * @author Kristian Popov
 */
@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

  private final GeminiService geminiService;

  @Autowired
  public GeminiController(GeminiService geminiService) {
    this.geminiService = geminiService;
  }

  /**
   * Asks Gemini AI API for a response.
   *
   * @param request The request body containing the prompt.
   * @return The response from Gemini.
   */
  @PostMapping("/ask")
  public ChatbotResponse askGeminiApi(@RequestBody ChatbotRequest request) {
    String responseText = geminiService.getGeminiResponse(request.getPrompt());
    return new ChatbotResponse(responseText);
  }
}