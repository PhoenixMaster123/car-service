package springboot.bg.harisauto.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BookingResponse.java - Response class for booking.
 *
 * @author Kristian Popov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {

  private UUID id;
  private UUID userId;
  private String status;
  private LocalDateTime bookingDate;
  private List<UUID> serviceIds;
  private UUID vehicleId;
  private String additionalNotes;
  private String paymentMethod;
  private String phoneNumber;
  private BigDecimal totalPrice;
  private String vehicleDescription;
  private String serviceNames;
}