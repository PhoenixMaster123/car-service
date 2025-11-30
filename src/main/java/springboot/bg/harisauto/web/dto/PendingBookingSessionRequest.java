package springboot.bg.harisauto.web.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PendingBookingSessionRequest.java - DTO class for pending booking session requests.
 *
 * @author Kristian Popov
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PendingBookingSessionRequest {

  private UUID userId;
  private BookingFormRequest formRequest;
  private List<UUID> serviceIds;
}
