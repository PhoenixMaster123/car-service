package springboot.bg.harisauto.common.config.ai;

import static com.google.genai.types.HarmBlockThreshold.Known.BLOCK_MEDIUM_AND_ABOVE;
import static com.google.genai.types.HarmCategory.Known.HARM_CATEGORY_DANGEROUS_CONTENT;
import static com.google.genai.types.HarmCategory.Known.HARM_CATEGORY_HARASSMENT;
import static com.google.genai.types.HarmCategory.Known.HARM_CATEGORY_HATE_SPEECH;
import static com.google.genai.types.HarmCategory.Known.HARM_CATEGORY_SEXUALLY_EXPLICIT;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import com.google.genai.types.SafetySetting;
import com.google.genai.types.ThinkingConfig;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * GeminiConfig.java - Configuration class for Gemini AI client.
 *
 * @author Kristian Popov
 */
@EnableRetry
@Configuration
public class GeminiConfig {

  @Value("${gemini.api-key}")
  private String apiKey;

  @Value("${gemini.model.temperature}")
  private Double defaultTemperature;

  @Value("${gemini.system-instruction}")
  private String defaultSystemInstruction;

  /** Gemini client bean. */
  @Bean
    public Client geminiClient() {
    return Client.builder()
      .apiKey(apiKey)
      .build();
  }

  /** Base config bean. */
  @Bean
  public GenerateContentConfig baseConfig() {
    List<SafetySetting> safetySettings = List.of(
        SafetySetting.builder()
            .category(HARM_CATEGORY_HATE_SPEECH)
            .threshold(BLOCK_MEDIUM_AND_ABOVE)
            .build(),
        SafetySetting.builder()
            .category(HARM_CATEGORY_DANGEROUS_CONTENT)
            .threshold(BLOCK_MEDIUM_AND_ABOVE)
            .build(),
        SafetySetting.builder()
            .category(HARM_CATEGORY_SEXUALLY_EXPLICIT)
            .threshold(BLOCK_MEDIUM_AND_ABOVE)
            .build(),
        SafetySetting.builder()
            .category(HARM_CATEGORY_HARASSMENT)
            .threshold(BLOCK_MEDIUM_AND_ABOVE)
            .build()
    );

    Content systemInstruction = Content.builder()
        .parts(Collections.singletonList(
            Part.builder().text(defaultSystemInstruction).build()
        )).build();

    return GenerateContentConfig.builder()
        .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0).build())
        .temperature(defaultTemperature.floatValue())
        .systemInstruction(systemInstruction)
        .safetySettings(safetySettings)
        .build();
  }
}