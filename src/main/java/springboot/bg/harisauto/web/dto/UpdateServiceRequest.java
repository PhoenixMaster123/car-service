package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateServiceRequest.java - DTO class for updating a service.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateServiceRequest {

  @NotNull(message = "Service ID is required")
  private UUID id;

  @NotBlank(message = "Service name is required")
  @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Description is required")
  @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
  private String description;

  @NotNull(message = "Base price is required")
  @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
  private BigDecimal basePrice;

  @NotNull(message = "Estimated duration is required")
  @Min(value = 1, message = "Estimated duration must be at least 1 minute")
  private Integer estimatedDurationInMinutes;

  @NotNull(message = "Category is required")
  private UUID categoryId;
}

