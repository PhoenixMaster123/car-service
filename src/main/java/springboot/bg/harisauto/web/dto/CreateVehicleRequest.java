package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateVehicleRequest.java - DTO class for creating a new vehicle.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {

  @NotBlank
  private String make;

  @NotBlank
  private String model;

  @NotNull
  private Year manufacturingYear;

  @NotBlank
  private String licensePlate;

  @NotBlank
  @Pattern(
      regexp = "^[A-HJ-NPR-Z0-9]{17}$",
      message = "VIN must be 17 characters long and exclude I, O, and Q")
  private String vin;

  private String color;
}