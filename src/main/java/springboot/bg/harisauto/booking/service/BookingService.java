package springboot.bg.harisauto.booking.service;

import feign.FeignException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.booking.client.BookingClient;
import springboot.bg.harisauto.booking.dto.request.BookingRequest;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.dto.response.GetBookingResponse;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.service.CatalogService;
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

      // Read the catalogue once and index it, rather than issuing one query per service
      // per booking. The catalogue is small and is already loaded on every services page.
      Map<UUID, String> serviceNamesById = catalogService.findAll().stream()
          .collect(Collectors.toMap(CarService::getId, CarService::getName));

      for (BookingResponse booking : bookings) {
        booking.setVehicleDescription(describeVehicle(booking.getVehicleId()));
        booking.setServiceNames(describeServices(booking.getServiceIds(), serviceNamesById));
      }
      return bookings;

    } catch (FeignException ex) {
      log.warn("[S2S Call] booking-service getBookings failed (returning empty list): status={} msg={}",
          ex.status(), ex.getMessage());
      return List.of();
    }
  }

  /**
   * Lists every booking, newest first, enriched with vehicle and service names.
   *
   * @return All bookings the booking service knows about.
   */
  public List<BookingResponse> getAllBookings() {

    try {
      GetBookingResponse response = client.getBookingsByStatus(null);
      if (response == null || response.getBookings() == null) {
        return List.of();
      }

      Map<UUID, String> serviceNamesById = catalogService.findAll().stream()
          .collect(Collectors.toMap(CarService::getId, CarService::getName));

      List<BookingResponse> bookings = new ArrayList<>(response.getBookings());
      for (BookingResponse booking : bookings) {
        booking.setVehicleDescription(describeVehicle(booking.getVehicleId()));
        booking.setServiceNames(describeServices(booking.getServiceIds(), serviceNamesById));
      }
      bookings.sort(Comparator.comparing(BookingResponse::getBookingDate,
          Comparator.nullsLast(Comparator.reverseOrder())));
      return bookings;

    } catch (FeignException ex) {
      log.error("[S2S Call] booking-service getBookings failed: status={} msg={}",
          ex.status(), ex.getMessage());
      throw new IllegalStateException(
          "Booking service is unreachable. Start the booking-service (default port 8082) "
          + "and try again.", ex);
    }
  }

  /**
   * Cancels a booking.
   *
   * @param bookingId The booking to cancel.
   */
  public void cancelBooking(UUID bookingId) {
    try {
      client.cancelBooking(bookingId);
      log.info("Booking {} cancelled", bookingId);
    } catch (FeignException ex) {
      log.error("[S2S Call] cancel of booking {} failed: status={}", bookingId, ex.status());
      throw new IllegalStateException("The booking could not be cancelled. Please try again.", ex);
    }
  }

  /**
   * Archives a booking.
   *
   * @param bookingId The booking to archive.
   */
  public void archiveBooking(UUID bookingId) {
    try {
      client.archiveBooking(bookingId);
      log.info("Booking {} archived", bookingId);
    } catch (FeignException ex) {
      log.error("[S2S Call] archive of booking {} failed: status={}", bookingId, ex.status());
      throw new IllegalStateException("The booking could not be archived. Please try again.", ex);
    }
  }

  /**
   * Builds a human-readable description of a booking's vehicle.
   *
   * @param vehicleId The vehicle id, may be null.
   * @return A description, or a placeholder when the vehicle is unknown.
   */
  private String describeVehicle(UUID vehicleId) {
    if (vehicleId == null) {
      return null;
    }
    // findById rather than getById: a booking can outlive the vehicle it refers to, and
    // that is a placeholder on the page, not a failure of the whole bookings list.
    return vehicleService.findById(vehicleId)
        .map(v -> v.getMake() + " " + v.getModel() + " (" + v.getLicensePlate() + ")")
        .orElseGet(() -> {
          log.warn("Booking references vehicle {} which no longer exists", vehicleId);
          return "Unknown Vehicle";
        });
  }

  /**
   * Joins the names of the booked services.
   *
   * @param serviceIds The booked service ids.
   * @param serviceNamesById Catalogue names indexed by service id.
   * @return A comma-separated list of names, or a placeholder when there are none.
   */
  private String describeServices(List<UUID> serviceIds, Map<UUID, String> serviceNamesById) {
    if (serviceIds == null || serviceIds.isEmpty()) {
      return "General Service";
    }
    return serviceIds.stream()
        .map(id -> serviceNamesById.getOrDefault(id, "Unknown Service"))
        .collect(Collectors.joining(", "));
  }
}
