package springboot.bg.harisauto.dev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.payment.service.CheckoutService;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

class DevPaymentControllerTest {

    private CheckoutService checkoutService;
    private ShoppingCart shoppingCart;
    private DevPaymentController controller;

    @BeforeEach
    void setup() {
        checkoutService = mock(CheckoutService.class);
        shoppingCart = new ShoppingCart();
        controller = new DevPaymentController(checkoutService, shoppingCart);
    }

    @Test
    void simulatePaymentSuccess_withoutPendingBooking_redirectsToBookings() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(null);

        ModelAndView mv = controller.simulatePaymentSuccess(session, mock(RedirectAttributes.class));

        assertThat(mv.getViewName()).isEqualTo("redirect:/bookings");
        verifyNoInteractions(checkoutService);
    }

    @Test
    void simulatePaymentSuccess_withPendingBooking_completesAndClearsSession() {
        PendingBookingSessionRequest pending = mock(PendingBookingSessionRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(pending);

        Invoice invoice = Invoice.builder()
                .id(UUID.randomUUID())
                .invoiceNumber("INV-2026-000001")
                .build();
        when(checkoutService.completePaidBooking(pending)).thenReturn(invoice);

        ModelAndView mv = controller.simulatePaymentSuccess(session, mock(RedirectAttributes.class));

        assertThat(mv.getViewName()).isEqualTo("redirect:/users/invoices");
        verify(checkoutService, times(1)).completePaidBooking(pending);
        verify(session, times(1)).removeAttribute("PENDING_BOOKING");
        assertThat(shoppingCart.getCount()).isZero();
    }

    @Test
    void simulatePaymentSuccess_whenCompletionFails_redirectsToCheckout() {
        PendingBookingSessionRequest pending = mock(PendingBookingSessionRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(pending);
        when(checkoutService.completePaidBooking(any()))
                .thenThrow(new IllegalStateException("booking-service down"));

        ModelAndView mv = controller.simulatePaymentSuccess(session, mock(RedirectAttributes.class));

        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verify(session, times(0)).removeAttribute("PENDING_BOOKING");
    }
}
