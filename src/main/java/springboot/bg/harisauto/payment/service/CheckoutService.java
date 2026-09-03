package springboot.bg.harisauto.payment.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.model.InvoiceStatus;
import springboot.bg.harisauto.invoice.service.InvoiceService;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.service.VehicleService;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

/**
 * CheckoutService.java - Turns a pending booking into a booking and a paid invoice.
 *
 * <p>Extracted from {@code PaymentController} so the production checkout flow and the
 * dev-only simulation endpoint can share it without the simulation living in a controller
 * that is registered in every environment.</p>
 *
 * @author Kristian Popov
 */
@Service
public class CheckoutService {

  private final ShoppingCart shoppingCart;
  private final BookingService bookingService;
  private final InvoiceService invoiceService;
  private final UserService userService;
  private final VehicleService vehicleService;

  /** Constructor. */
  public CheckoutService(ShoppingCart shoppingCart,
                         BookingService bookingService,
                         InvoiceService invoiceService,
                         UserService userService,
                         VehicleService vehicleService) {
    this.shoppingCart = shoppingCart;
    this.bookingService = bookingService;
    this.invoiceService = invoiceService;
    this.userService = userService;
    this.vehicleService = vehicleService;
  }

  /**
   * Creates the booking and a PAID invoice. Used by the dev simulation only.
   *
   * @param pending The booking held in the session.
   * @return The generated invoice.
   */
  public Invoice completePaidBooking(PendingBookingSessionRequest pending) {
    return completeBooking(pending, InvoiceStatus.PAID, null);
  }

  /**
   * Creates the booking and its invoice for a pending checkout.
   *
   * @param pending The booking held in the session.
   * @param status The invoice status the caller has established.
   * @param paymentReference The payment intent id, when one is known.
   * @return The generated invoice.
   */
  public Invoice completeBooking(PendingBookingSessionRequest pending,
                                 InvoiceStatus status,
                                 String paymentReference) {

    BigDecimal totalPrice = shoppingCart.getTotal();
    List<CarService> cartItems = List.copyOf(shoppingCart.getItems());

    bookingService.createBooking(
        pending.getUserId(),
        pending.getFormRequest().getBookingDate(),
        pending.getServiceIds(),
        pending.getFormRequest().getVehicleId(),
        pending.getFormRequest().getAdditionalNotes(),
        pending.getFormRequest().getPaymentMethod(),
        pending.getFormRequest().getPhoneNumber(),
        totalPrice,
        status.name()
    );

    User user = userService.getById(pending.getUserId());
    Vehicle vehicle = vehicleService.getById(pending.getFormRequest().getVehicleId());

    return invoiceService.generate(
        user,
        vehicle,
        cartItems,
        pending.getFormRequest().getBookingDate(),
        pending.getFormRequest().getPaymentMethod(),
        status,
        paymentReference,
        null
    );
  }
}
