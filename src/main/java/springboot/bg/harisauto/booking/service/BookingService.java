package springboot.bg.harisauto.booking.service;

import feign.FeignException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.booking.client.BookingClient;
import springboot.bg.harisauto.booking.dto.request.BookingRequest;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.dto.response.GetBookingResponse;
import springboot.bg.harisauto.service.service.CatalogService;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.service.VehicleService;

/**
 * BookingService.java - Service class for managing bookings.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class BookingService {

  private final BookingClient client;
  private final VehicleService vehicleService;
  private final CatalogService catalogService;

  @Autowired
  public BookingService(BookingClient client, VehicleService vehicleService, CatalogService catalogService) {
    this.client = client;
    this.vehicleService = vehicleService;
    this.catalogService = catalogService;
  }

  /**
   * Create booking.
   *
   * @param userId - user id
   * @param bookingDate - booking date
   * @param serviceIds - service ids
   * @param vehicleId - vehicle id
   * @param additionalNotes - additional notes
   * @param paymentMethod - payment method
   * @param phoneNumber - phone number
   * @param totalPrice - total price
   * @param status - status
   */
  public void createBooking(UUID userId, LocalDateTime bookingDate,
      List<UUID> serviceIds, UUID vehicleId, String additionalNotes,
      String paymentMethod, String phoneNumber, BigDecimal totalPrice, String status) {

    BookingRequest request = BookingRequest.builder()
        .userId(userId)
        .bookingDate(bookingDate)
        .serviceIds(serviceIds)
        .vehicleId(vehicleId)
        .additionalNotes(additionalNotes)
        .paymentMethod(paymentMethod)
        .phoneNumber(phoneNumber)
        .totalPrice(totalPrice)
        .status(status)
        .build();

    try {
      client.createBooking(request);
    } catch (FeignException ex) {
      log.error("[S2S Call] booking-service createBooking failed: status={} msg={}", ex.status(), ex.getMessage());
      throw new IllegalStateException(
          "Booking service is unreachable. Start the booking-service (default port 8082) and try again.", ex);
    }
  }

  /**
   * Get bookings by user id.
   *
   * @param userId - user id
   * @return List of bookings
   */
  public List<BookingResponse> getBookingsByUser(UUID userId) {

    try {

      GetBookingResponse response = client.getBookings(userId);
      if (response == null || response.getBookings() == null) {
        return List.of();
      }

      List<BookingResponse> bookings = response.getBookings();

      for (BookingResponse booking : bookings) {

        if (booking.getVehicleId() != null) {

          try {
            Vehicle v = vehicleService.getById(booking.getVehicleId());
            booking.setVehicleDescription(v.getMake() + " " + v.getModel() + " (" + v.getLicensePlate() + ")");
          } catch (Exception e) {
            booking.setVehicleDescription("Unknown Vehicle");
          }
        }

        if (booking.getServiceIds() != null && !booking.getServiceIds().isEmpty()) {

          try {
            String names = booking.getServiceIds().stream()
                .map(id -> catalogService.getById(id).getName())
                .collect(Collectors.joining(", "));

            booking.setServiceNames(names);
          } catch (Exception e) {
            booking.setServiceNames("Service details unavailable");
          }
        } else {
          booking.setServiceNames("General Service");
        }
      }
      return bookings;

    } catch (FeignException ex) {
      log.warn("[S2S Call] booking-service getBookings failed (returning empty list): status={} msg={}",
          ex.status(), ex.getMessage());
      return List.of();
    }
  }
}