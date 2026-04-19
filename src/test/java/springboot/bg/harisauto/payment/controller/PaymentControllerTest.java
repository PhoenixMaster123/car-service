package springboot.bg.harisauto.payment.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.payment.client.PaymentClient;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;
import springboot.bg.harisauto.web.dto.BookingFormRequest;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    private PaymentClient paymentClient;
    private ShoppingCart shoppingCart;
    private BookingService bookingService;
    private PaymentController controller;

    @BeforeEach
    void setup() {
        paymentClient = mock(PaymentClient.class);
        shoppingCart = new ShoppingCart();
        bookingService = mock(BookingService.class);
        controller = new PaymentController(paymentClient, shoppingCart, bookingService);
    }

    @Test
    void proxyCreatePaymentIntent_delegatesToClient() {
        PaymentRequest req = new PaymentRequest();
        PaymentResponse resp = new PaymentResponse();
        when(paymentClient.createPaymentIntent(req)).thenReturn(resp);
        PaymentResponse result = controller.proxyCreatePaymentIntent(req);
        assertThat(result).isSameAs(resp);
        verify(paymentClient, times(1)).createPaymentIntent(req);
    }

    @Test
    void showCheckoutPage_withoutPendingInSession_redirectsToBookings() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(null);
        ModelAndView mv = controller.showCheckoutPage(session);
        assertThat(mv.getViewName()).isEqualTo("redirect:/bookings");
    }

    @Test
    void showCheckoutPage_withPending_showsCheckoutView() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(new Object());
        ModelAndView mv = controller.showCheckoutPage(session);
        assertThat(mv.getViewName()).isEqualTo("public/checkout");
        assertThat(mv.getModel()).containsKeys("cart", "bookingRequest", "stripePublicKey");
    }

    @Test
    void processPayment_success_createsBooking_clearsCart_andRedirects() {
        // Put items in cart so total > 0 and ensure clear() gets called
        shoppingCart.addItem(springboot.bg.harisauto.service.model.CarService.builder()
                .id(UUID.randomUUID()).name("Oil").basePrice(new BigDecimal("15.00")).build());
        shoppingCart.addItem(springboot.bg.harisauto.service.model.CarService.builder()
                .id(UUID.randomUUID()).name("Tire").basePrice(new BigDecimal("25.00")).build());

        // Prepare pending session
        BookingFormRequest form = BookingFormRequest.builder()
                .bookingDate(LocalDateTime.now().plusDays(1))
                .vehicleId(UUID.randomUUID())
                .additionalNotes("n")
                .paymentMethod("CARD")
                .phoneNumber("+111")
                .build();
        PendingBookingSessionRequest pending = new PendingBookingSessionRequest(UUID.randomUUID(), form,
                List.of(UUID.randomUUID()));

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(pending);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        ModelAndView mv = controller.processPayment("succeeded", redirectAttributes, session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/users/my-bookings");
        verify(bookingService, times(1)).createBooking(
                eq(pending.getUserId()),
                eq(form.getBookingDate()),
                eq(pending.getServiceIds()),
                eq(form.getVehicleId()),
                eq(form.getAdditionalNotes()),
                eq(form.getPaymentMethod()),
                eq(form.getPhoneNumber()),
                eq(new BigDecimal("40.00")),
                eq("PAID")
        );
        verify(session, times(1)).removeAttribute("PENDING_BOOKING");
        assertThat(shoppingCart.getCount()).isZero();
    }

    @Test
    void processPayment_success_butNoPending_redirectsToContact() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(null);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        ModelAndView mv = controller.processPayment("succeeded", redirectAttributes, session);
        assertThat(mv.getViewName()).isEqualTo("redirect:/contact-us");
        verifyNoInteractions(bookingService);
    }

    @Test
    void processPayment_failedOrCancelled_redirectsToCheckout() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        ModelAndView mv = controller.processPayment("failed", redirectAttributes, session);
        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verifyNoInteractions(bookingService);
    }
}
