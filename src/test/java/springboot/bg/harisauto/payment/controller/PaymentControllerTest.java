package springboot.bg.harisauto.payment.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.dev.DevModeProperties;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.model.InvoiceStatus;
import springboot.bg.harisauto.invoice.service.InvoiceService;
import springboot.bg.harisauto.payment.PaymentVerificationProperties;
import springboot.bg.harisauto.payment.client.PaymentClient;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.dto.response.PaymentIntentResponse;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;
import springboot.bg.harisauto.payment.service.CheckoutService;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.service.VehicleService;
import springboot.bg.harisauto.web.dto.BookingFormRequest;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

class PaymentControllerTest {

    private static final String INTENT_ID = "pi_3AbcDef";
    private static final String CLIENT_SECRET = INTENT_ID + "_secret_xyz";

    private PaymentClient paymentClient;
    private ShoppingCart shoppingCart;
    private BookingService bookingService;
    private InvoiceService invoiceService;
    private UserService userService;
    private VehicleService vehicleService;
    private DevModeProperties devModeProperties;
    private PaymentVerificationProperties verificationProperties;
    private PaymentController controller;

    @BeforeEach
    void setup() {
        paymentClient = mock(PaymentClient.class);
        shoppingCart = new ShoppingCart();
        bookingService = mock(BookingService.class);
        invoiceService = mock(InvoiceService.class);
        userService = mock(UserService.class);
        vehicleService = mock(VehicleService.class);
        devModeProperties = new DevModeProperties();
        verificationProperties = new PaymentVerificationProperties();
        // Real CheckoutService over the same mocks, so assertions still reach the booking
        // and invoice calls rather than stopping at a mocked facade.
        CheckoutService checkoutService = new CheckoutService(shoppingCart, bookingService,
                invoiceService, userService, vehicleService);
        controller = new PaymentController(paymentClient, shoppingCart, checkoutService,
                devModeProperties, verificationProperties);
    }

    private PendingBookingSessionRequest pendingBooking() {
        BookingFormRequest form = BookingFormRequest.builder()
                .bookingDate(LocalDateTime.now().plusDays(1))
                .vehicleId(UUID.randomUUID())
                .additionalNotes("n")
                .paymentMethod("CARD")
                .phoneNumber("+111")
                .build();
        return new PendingBookingSessionRequest(UUID.randomUUID(), form, List.of(UUID.randomUUID()));
    }

    private HttpSession sessionWith(PendingBookingSessionRequest pending, String storedIntentId) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(pending);
        when(session.getAttribute("PAYMENT_INTENT_ID")).thenReturn(storedIntentId);
        when(session.getAttribute("PAYMENT_INTENT_AMOUNT")).thenReturn(4000L);
        return session;
    }

    private void stubInvoiceGeneration(PendingBookingSessionRequest pending) {
        shoppingCart.addItem(CarService.builder()
                .id(UUID.randomUUID()).name("Oil").basePrice(new BigDecimal("15.00")).build());
        shoppingCart.addItem(CarService.builder()
                .id(UUID.randomUUID()).name("Tire").basePrice(new BigDecimal("25.00")).build());

        User user = new User();
        user.setId(pending.getUserId());
        user.setEmail("t@example.com");
        Vehicle vehicle = Vehicle.builder()
                .id(pending.getFormRequest().getVehicleId()).make("Audi").model("A4").build();

        when(userService.getById(pending.getUserId())).thenReturn(user);
        when(vehicleService.getById(pending.getFormRequest().getVehicleId())).thenReturn(vehicle);
        when(invoiceService.generate(any(), any(), anyList(), any(), anyString(),
                any(InvoiceStatus.class), any(), any()))
                .thenReturn(Invoice.builder().invoiceNumber("INV-2026-000001").build());
    }

    @Test
    void proxyCreatePaymentIntent_storesTheIntentItCreatedOnTheSession() {
        PaymentRequest req = new PaymentRequest();
        req.setAmount(4000L);
        when(paymentClient.createPaymentIntent(req))
                .thenReturn(PaymentResponse.builder().clientSecret(CLIENT_SECRET).build());
        HttpSession session = mock(HttpSession.class);

        controller.proxyCreatePaymentIntent(req, session);

        verify(session).setAttribute("PAYMENT_INTENT_ID", INTENT_ID);
        verify(session).setAttribute("PAYMENT_INTENT_AMOUNT", 4000L);
    }

    @Test
    void processPayment_whenRedirectStatusIsNotSucceeded_redirectsToCheckout() {
        ModelAndView mv = controller.processPayment("failed", INTENT_ID,
                mock(RedirectAttributes.class), mock(HttpSession.class));

        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verifyNoInteractions(bookingService);
    }

    @Test
    void processPayment_withoutPendingBooking_redirectsToContact() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(null);

        ModelAndView mv = controller.processPayment("succeeded", INTENT_ID,
                mock(RedirectAttributes.class), session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/contact-us");
        verifyNoInteractions(bookingService);
    }

    @Test
    void processPayment_whenReturnedIntentDoesNotMatchTheSession_createsNothing() {
        PendingBookingSessionRequest pending = pendingBooking();
        HttpSession session = sessionWith(pending, INTENT_ID);

        ModelAndView mv = controller.processPayment("succeeded", "pi_someoneElse",
                mock(RedirectAttributes.class), session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verifyNoInteractions(bookingService);
        verifyNoInteractions(invoiceService);
    }

    @Test
    void processPayment_withVerificationOff_recordsPendingNeverPaid() {
        PendingBookingSessionRequest pending = pendingBooking();
        stubInvoiceGeneration(pending);
        HttpSession session = sessionWith(pending, INTENT_ID);
        verificationProperties.setEnabled(false);

        ModelAndView mv = controller.processPayment("succeeded", INTENT_ID,
                mock(RedirectAttributes.class), session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/users/invoices");
        // The whole point: a query parameter must never produce a PAID invoice.
        verify(invoiceService).generate(any(), any(), anyList(), any(), anyString(),
                eq(InvoiceStatus.PENDING), eq(INTENT_ID), any());
        verify(bookingService, times(1)).createBooking(any(), any(), anyList(), any(),
                any(), any(), any(), any(), eq("PENDING"));
        verifyNoInteractions(paymentClient);
    }

    @Test
    void processPayment_withVerificationOn_andConfirmedIntent_marksPaid() {
        PendingBookingSessionRequest pending = pendingBooking();
        stubInvoiceGeneration(pending);
        HttpSession session = sessionWith(pending, INTENT_ID);
        verificationProperties.setEnabled(true);
        when(paymentClient.getPaymentIntent(INTENT_ID)).thenReturn(PaymentIntentResponse.builder()
                .id(INTENT_ID).status("succeeded").amountReceived(4000L).currency("eur").build());

        ModelAndView mv = controller.processPayment("succeeded", INTENT_ID,
                mock(RedirectAttributes.class), session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/users/invoices");
        verify(invoiceService).generate(any(), any(), anyList(), any(), anyString(),
                eq(InvoiceStatus.PAID), eq(INTENT_ID), any());
    }

    @Test
    void processPayment_withVerificationOn_andUnpaidIntent_createsNothing() {
        PendingBookingSessionRequest pending = pendingBooking();
        HttpSession session = sessionWith(pending, INTENT_ID);
        verificationProperties.setEnabled(true);
        when(paymentClient.getPaymentIntent(INTENT_ID)).thenReturn(PaymentIntentResponse.builder()
                .id(INTENT_ID).status("requires_payment_method").build());

        ModelAndView mv = controller.processPayment("succeeded", INTENT_ID,
                mock(RedirectAttributes.class), session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verifyNoInteractions(bookingService);
        verifyNoInteractions(invoiceService);
    }

    @Test
    void processPayment_withVerificationOn_andWrongAmount_createsNothing() {
        PendingBookingSessionRequest pending = pendingBooking();
        HttpSession session = sessionWith(pending, INTENT_ID);
        verificationProperties.setEnabled(true);
        when(paymentClient.getPaymentIntent(INTENT_ID)).thenReturn(PaymentIntentResponse.builder()
                .id(INTENT_ID).status("succeeded").amountReceived(1L).currency("eur").build());

        ModelAndView mv = controller.processPayment("succeeded", INTENT_ID,
                mock(RedirectAttributes.class), session);

        assertThat(mv.getViewName()).isEqualTo("redirect:/checkout");
        verifyNoInteractions(bookingService);
        verifyNoInteractions(invoiceService);
    }

    @Test
    void showCheckoutPage_withoutPendingInSession_redirectsToBookings() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("PENDING_BOOKING")).thenReturn(null);
        assertThat(controller.showCheckoutPage(session).getViewName())
                .isEqualTo("redirect:/bookings");
    }
}
