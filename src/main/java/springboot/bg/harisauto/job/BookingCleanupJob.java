package springboot.bg.harisauto.job;

import feign.FeignException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import springboot.bg.harisauto.booking.client.BookingClient;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.dto.response.GetBookingResponse;

/**
 * Runs scheduled jobs to cancel expired bookings and archive old bookings.
 *
 * @author Kristian Popov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupJob {

  private final BookingClient client;

  /**
   * CANCEL EXPIRED BOOKINGS
   * Runs every day at 2:00 AM.
   */
  @Scheduled(cron = "0 0 2 * * *")
  public void cancelExpiredBookings() {

    log.info("Starting expired bookings cleanup job...");

    try {

      GetBookingResponse response = client.getBookingsByStatus("PENDING");

      if (response == null || response.getBookings() == null || response.getBookings().isEmpty()) {
        log.info("No pending bookings found.");
        return;
      }

      LocalDateTime now = LocalDateTime.now();
      int cancelCount = 0;

      for (BookingResponse booking : response.getBookings()) {

        if (booking.getBookingDate() == null) {
          continue;
        }

        if (booking.getBookingDate().isBefore(now)) {

          try {

            log.info("Cancelling expired booking ID: {}", booking.getId());

            client.cancelBooking(booking.getId());
            cancelCount++;

          } catch (FeignException e) {
            log.error("Failed to cancel booking {}: Status {}", booking.getId(), e.status());
          }
        }
      }
      log.info("Cleanup job finished. Cancelled {} bookings.", cancelCount);

    } catch (FeignException e) {
      log.error("Failed to connect to Booking Service: {}", e.getMessage());
    }
  }

  /**
   * ARCHIVE OLD BOOKINGS
   * Runs every Sunday at 3:00 AM.
   */
  @Scheduled(cron = "0 0 3 * * SUN")
  public void archiveOldBookings() {

    log.info("Starting old bookings archival job...");

    try {

      GetBookingResponse response = client.getBookingsByStatus("COMPLETED");

      if (response == null || response.getBookings() == null) {
        return;
      }

      LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
      int archiveCount = 0;

      for (BookingResponse booking : response.getBookings()) {

        if (booking.getBookingDate() == null) {
          continue;
        }

        if (booking.getBookingDate().isBefore(cutoffDate)) {

          try {

            log.debug("Archiving old booking ID: {}", booking.getId());

            client.archiveBooking(booking.getId());
            archiveCount++;

          } catch (FeignException e) {
            log.error("Failed to archive booking {}: {}", booking.getId(), e.getMessage());
          }
        }
      }
      log.info("Archival job finished. Processed {} bookings.", archiveCount);

    } catch (FeignException e) {
      log.error("Failed to fetch completed bookings: {}", e.getMessage());
    }
  }
}