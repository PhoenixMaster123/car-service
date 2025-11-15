package springboot.bg.harisauto.chatbot.service;

import com.google.genai.Client;
import com.google.genai.errors.ServerException;
import com.google.genai.types.Candidate;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.GroundingMetadata;
import com.google.genai.types.Part;
import com.google.genai.types.Tool;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * GeminiService.java - Service for interacting with Gemini AI API.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class GeminiService {

  private final Client client;
  private final GenerateContentConfig config;

  @Value("${gemini.model.name}")
  private String modelName;

  /* Constructor */
  public GeminiService(Client client, GenerateContentConfig config) {
    this.client = client;
    this.config = config;
  }

  /**
   * Asks Gemini AI API for a response.
   *
   * @param prompt The prompt to send to Gemini.
   * @return The response text from Gemini.
   */
  @Retryable(
      retryFor = { ServerException.class },
      maxAttempts = 3,
      backoff = @Backoff(delay = 2000, multiplier = 2)
  )
  public String getGeminiResponse(String prompt) {
    String currentTime = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd HH:mm"));

    List<Part> combinedInstructions = new ArrayList<>();

    config.systemInstruction().ifPresent(content -> {
      combinedInstructions.addAll(content.parts().orElse(Collections.emptyList()));
    });

    String dynamicContext = String.format(
        " SYSTEM CONTEXT: Current time is %s. "
        + " COMMANDS: "
        + " 1. If the user asks about Weather, Traffic, or Locations: "
        + "USE GOOGLE SEARCH (Critical for safety). "
        + " 2. If the user asks about General News: USE GOOGLE SEARCH. "
        + "(Reason: Brief news updates keep the driver informed and awake). "
        + " 3. Keep all news summaries short (2-3 sentences max) so the driver stays focused.",
        currentTime
    );

    combinedInstructions.add(Part.builder().text(dynamicContext).build());

    GenerateContentConfig requestConfig = GenerateContentConfig.builder()
        .temperature(config.temperature().orElse(0.7f))
        .safetySettings(config.safetySettings().orElse(Collections.emptyList()))
        .systemInstruction(Content.builder().parts(combinedInstructions).build())
        .tools(Collections.singletonList(
            Tool.builder().googleSearch(GoogleSearch.builder().build()).build()
        )).build();

    GenerateContentResponse response = client.models.generateContent(
        modelName,
        Content.builder().parts(Collections.singletonList(
                Part.builder().text(prompt).build())).build(),
        requestConfig
    );

    response.candidates().ifPresent(candidatesList -> {
      if (!candidatesList.isEmpty()) {
        Candidate candidate = candidatesList.get(0);

        candidate.groundingMetadata().flatMap(GroundingMetadata::groundingChunks)
            .ifPresent(chunks -> {
              log.info("✅ Google Search Triggered!");
              log.info("   Number of sources: {}", chunks.size());
              if (!chunks.isEmpty()) {
                chunks.get(0).web().ifPresent(web ->
                    log.info("   Source: {}", web.title())
                );
              }
            });

        if (candidate.groundingMetadata().isEmpty()) {
          log.info("⚠️ No Google Search performed.");
        }
      }
    });
    return response.text();
  }
}