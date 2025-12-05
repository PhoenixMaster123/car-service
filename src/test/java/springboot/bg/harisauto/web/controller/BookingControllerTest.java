package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.validation.BindingResult;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.service.VehicleService;
import springboot.bg.harisauto.web.dto.BookingFormRequest;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    private UserService userService;
    private VehicleService vehicleService;
    private ShoppingCart shoppingCart;
    private BookingService bookingService;
    private BookingController controller;

    private AuthenticationMetaData auth;
    private User user;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        vehicleService = mock(VehicleService.class);
        shoppingCart = spy(new ShoppingCart());
        bookingService = mock(BookingService.class);
        controller = new BookingController(userService, vehicleService, shoppingCart, bookingService);

        user = new User();
        UUID userId = UUID.randomUUID();
        // The AuthenticationMetaData has a constructor taking (UUID, email, password, role, etc.)
        // For testing, we will mock and stub getUserId.
        auth = mock(AuthenticationMetaData.class);
        when(auth.getUserId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(user);
    }

    @Test
    void showBookingPage_returnsBookingViewWithCartAndVehicles() {
        ModelAndView mv = controller.showBookingPage(auth);
        assertThat(mv.getViewName()).isEqualTo("/public/booking");
        assertThat(mv.getModel()).containsKeys("cart", "bookingFormRequest", "vehicles");
        assertThat(mv.getModel().get("cart")).isSameAs(shoppingCart);
    }

    @Test
    void createBooking_whenValidationErrors_returnsBookingView() {
        BookingFormRequest req = new BookingFormRequest();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        HttpSession session = mock(HttpSession.class);

        ModelAndView mv = controller.createBooking(auth, req, result, session);

        assertThat(mv.getViewName()).isEqualTo("/public/booking");
        assertThat(mv.getModel()).containsKeys("cart", "vehicles");
        verifyNoInteractions(bookingService);
    }

    @Test
    void createBooking_whenCartEmpty_returnsErrorOnBookingView() {
        BookingFormRequest req = new BookingFormRequest();
        req.setPaymentMethod("CASH");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        HttpSession session = mock(HttpSession.class);

        // cart empty by default
        ModelAndView mv = controller.createBooking(auth, req, result, session);

        assertThat(mv.getViewName()).isEqualTo("/public/booking");
        assertThat(mv.getModel()).containsKey("error");
        verifyNoInteractions(bookingService);
    }

    @Test
    void createBooking_cashFlow_createsBookingClearsCartAndRedirects() {
        // Add two services in cart
        CarService s1 = CarService.builder().id(UUID.randomUUID()).name("Oil").basePrice(new BigDecimal("10.00")).build();
        CarService s2 = CarService.builder().id(UUID.randomUUID()).name("Tire").basePrice(new BigDecimal("20.00")).build();
        shoppingCart.addItem(s1);
        shoppingCart.addItem(s2);

        BookingFormRequest req = BookingFormRequest.builder()
                .bookingDate(LocalDateTime.now().plusDays(1))
                .vehicleId(UUID.randomUUID())
                .additionalNotes("notes")
                .paymentMethod("CASH")
                .phoneNumber("+123")
                .build();

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        HttpSession session = mock(HttpSession.class);

        ModelAndView mv = controller.createBooking(auth, req, result, session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/users/my-bookings");
        // Ensure booking created with total 30.00 and status PENDING
        verify(bookingService, times(1)).createBooking(
                eq(auth.getUserId()),
                eq(req.getBookingDate()),
                anyList(),
                eq(req.getVehicleId()),
                eq(req.getAdditionalNotes()),
                eq("CASH"),
                eq("+123"),
                eq(new BigDecimal("30.00")),
                eq("PENDING")
        );
        assertThat(shoppingCart.getCount()).isZero();
    }

    @Test
    void createBooking_cardFlow_setsPendingInSessionAndRedirectsToCheckout() {
        // Add one service so cart isn't empty
        CarService s1 = CarService.builder().id(UUID.randomUUID()).name("Oil").basePrice(new BigDecimal("50.00")).build();
        shoppingCart.addItem(s1);

        BookingFormRequest req = BookingFormRequest.builder()
                .bookingDate(LocalDateTime.now().plusDays(1))
                .vehicleId(UUID.randomUUID())
                .additionalNotes("notes")
                .paymentMethod("CARD")
                .phoneNumber("+123")
                .build();

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        HttpSession session = mock(HttpSession.class);

        ModelAndView mv = controller.createBooking(auth, req, result, session);
        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verify(session, times(1)).setAttribute(eq("PENDING_BOOKING"), any());
        verifyNoInteractions(bookingService);
    }
}
