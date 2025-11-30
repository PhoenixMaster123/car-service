package springboot.bg.harisauto.booking.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BookingRequest.java - DTO class for booking request.
 *
 * @author Kristian Popov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {

  private UUID userId;
  private LocalDateTime bookingDate;
  private List<UUID> serviceIds;
  private UUID vehicleId;
  private String additionalNotes;
  private String paymentMethod;
  private String phoneNumber;
  private BigDecimal totalPrice;
  private String status;
}