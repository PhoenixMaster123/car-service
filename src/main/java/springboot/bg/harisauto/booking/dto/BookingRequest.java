package springboot.bg.harisauto.booking.dto;

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

  private LocalDateTime bookingDate;
  private List<UUID> serviceIds;
  private UUID vehicleId;
  private String notes;
  private String paymentMethod;
  private String phoneNumber;
}