package springboot.bg.harisauto.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import springboot.bg.harisauto.booking.client.BookingClient;
import springboot.bg.harisauto.booking.dto.request.BookingRequest;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.dto.response.GetBookingResponse;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.service.CatalogService;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.service.VehicleService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private BookingClient bookingClient;
    private VehicleService vehicleService;
    private CatalogService catalogService;
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingClient = mock(BookingClient.class);
        vehicleService = mock(VehicleService.class);
        catalogService = mock(CatalogService.class);
        bookingService = new BookingService(bookingClient, vehicleService, catalogService);
    }

    @Test
    void createBooking_sendsRequestToClient() {
        UUID userId = UUID.randomUUID();
        LocalDateTime bookingDate = LocalDateTime.now();
        List<UUID> serviceIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        UUID vehicleId = UUID.randomUUID();
        String notes = "Test";
        String method = "CASH";
        String phone = "+123";
        BigDecimal total = new BigDecimal("99.99");
        String status = "PENDING";

        bookingService.createBooking(userId, bookingDate, serviceIds, vehicleId, notes, method, phone, total, status);

        ArgumentCaptor<BookingRequest> reqCap = ArgumentCaptor.forClass(BookingRequest.class);
        verify(bookingClient, times(1)).createBooking(reqCap.capture());
        BookingRequest sent = reqCap.getValue();
        assertThat(sent.getUserId()).isEqualTo(userId);
        assertThat(sent.getBookingDate()).isEqualTo(bookingDate);
        assertThat(sent.getServiceIds()).isEqualTo(serviceIds);
        assertThat(sent.getVehicleId()).isEqualTo(vehicleId);
        assertThat(sent.getAdditionalNotes()).isEqualTo(notes);
        assertThat(sent.getPaymentMethod()).isEqualTo(method);
        assertThat(sent.getPhoneNumber()).isEqualTo(phone);
        assertThat(sent.getTotalPrice()).isEqualTo(total);
        assertThat(sent.getStatus()).isEqualTo(status);
    }

    @Test
    void getBookingsByUser_whenResponseNull_returnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(bookingClient.getBookings(userId)).thenReturn(null);

        List<BookingResponse> result = bookingService.getBookingsByUser(userId);
        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsByUser_happyPath_enrichesVehicleAndServiceNames() {
        UUID userId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        UUID service1 = UUID.randomUUID();
        UUID service2 = UUID.randomUUID();

        BookingResponse b = BookingResponse.builder()
                .id(bookingId)
                .userId(userId)
                .vehicleId(vehicleId)
                .serviceIds(List.of(service1, service2))
                .build();
        GetBookingResponse response = new GetBookingResponse(new ArrayList<>(List.of(b)));
        when(bookingClient.getBookings(userId)).thenReturn(response);

        Vehicle v = new Vehicle();
        v.setMake("BMW");
        v.setModel("X5");
        v.setLicensePlate("CB1234AB");
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(v));

        CarService s1 = new CarService();
        s1.setId(service1);
        s1.setName("Oil change");
        CarService s2 = new CarService();
        s2.setId(service2);
        s2.setName("Tire rotation");
        when(catalogService.findAll()).thenReturn(List.of(s1, s2));

        List<BookingResponse> result = bookingService.getBookingsByUser(userId);

        assertThat(result).hasSize(1);
        BookingResponse enriched = result.get(0);
        assertThat(enriched.getVehicleDescription()).contains("BMW X5 (CB1234AB)");
        assertThat(enriched.getServiceNames()).isEqualTo("Oil change, Tire rotation");
    }

    @Test
    void getBookingsByUser_handlesNullOrEmptyServicesAndVehicleErrors() {
        UUID userId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();

        BookingResponse b1 = BookingResponse.builder()
                .id(bookingId)
                .userId(userId)
                .vehicleId(vehicleId)
                .serviceIds(null)
                .build();
        GetBookingResponse response = new GetBookingResponse(new ArrayList<>(List.of(b1)));
        when(bookingClient.getBookings(userId)).thenReturn(response);

        // A missing vehicle comes back as an empty Optional, not as an exception.
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.empty());

        List<BookingResponse> result = bookingService.getBookingsByUser(userId);

        assertThat(result).hasSize(1);
        BookingResponse enriched = result.get(0);
        assertThat(enriched.getVehicleDescription()).isEqualTo("Unknown Vehicle");
        assertThat(enriched.getServiceNames()).isEqualTo("General Service");
    }

    @Test
    void getBookingsByUser_whenServiceNotInCatalogue_namesItUnknown() {
        UUID userId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        UUID sId = UUID.randomUUID();

        BookingResponse b = BookingResponse.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .serviceIds(List.of(sId))
                .build();
        GetBookingResponse response = new GetBookingResponse(new ArrayList<>(List.of(b)));
        when(bookingClient.getBookings(userId)).thenReturn(response);

        Vehicle v = new Vehicle();
        v.setMake("VW");
        v.setModel("Golf");
        v.setLicensePlate("X1234YY");
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(v));

        // The service is no longer in the catalogue, so it cannot be named.
        when(catalogService.findAll()).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByUser(userId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getServiceNames()).isEqualTo("Unknown Service");
    }
}
