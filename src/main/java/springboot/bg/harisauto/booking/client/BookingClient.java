package springboot.bg.harisauto.booking.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import springboot.bg.harisauto.booking.dto.request.BookingRequest;
import springboot.bg.harisauto.booking.dto.response.GetBookingResponse;

/**
 * BookingClient.java - Client interface for booking service.
 *
 * @author Kristian Popov
 */
@FeignClient(name = "booking-service", url = "${booking-service.url}/api/v1")
public interface BookingClient {

  /** Creates a new booking. */
  @PostMapping("/bookings")
  ResponseEntity<Void> createBooking(@RequestBody BookingRequest bookingRequest);

  /** Gets all bookings for a user. */
  @GetMapping("/bookings")
  GetBookingResponse getBookings(@RequestParam("userId") UUID userId);

  /** Fetch bookings by status. */
  @GetMapping("/bookings")
  GetBookingResponse getBookingsByStatus(@RequestParam(value = "status", required = false) String status);

  /** Batch Cancel. */
  @PostMapping("/bookings/{id}/cancel")
  ResponseEntity<Void> cancelBooking(@PathVariable("id") UUID bookingId);

  /** Archive a specific booking. */
  @PostMapping("/bookings/{id}/archive")
  ResponseEntity<Void> archiveBooking(@PathVariable("id") UUID bookingId);
}