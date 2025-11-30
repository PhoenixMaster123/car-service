package springboot.bg.harisauto.booking.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GetBookingResponse.java - Response class for getting all bookings.
 *
 * @author Kristian Popov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBookingResponse {

  private List<BookingResponse> bookings;
}