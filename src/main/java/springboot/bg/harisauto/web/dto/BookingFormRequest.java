package springboot.bg.harisauto.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * BookingFormRequest.java - DTO class for booking form requests.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingFormRequest {

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  private LocalDateTime bookingDate;
  private UUID vehicleId;
  private String additionalNotes;
  private String paymentMethod;
  private String phoneNumber;
}