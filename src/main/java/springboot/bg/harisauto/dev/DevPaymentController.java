package springboot.bg.harisauto.dev;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.payment.service.CheckoutService;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

/**
 * DevPaymentController.java - Simulates a successful payment without calling Stripe.
 *
 * <p>Registered only under the "dev" profile. Previously this mapping lived in
 * {@code PaymentController} and was guarded at runtime by {@code app.dev-mode.enabled},
 * which meant a free-checkout endpoint was present in the production artifact and one
 * property away from being live. Gating on the profile removes the mapping entirely
 * outside development.</p>
 *
 * @author Kristian Popov
 */
@Controller
@Profile("dev")
public class DevPaymentController {

  private final CheckoutService checkoutService;
  private final ShoppingCart shoppingCart;

  /** Constructor. */
  public DevPaymentController(CheckoutService checkoutService, ShoppingCart shoppingCart) {
    this.checkoutService = checkoutService;
    this.shoppingCart = shoppingCart;
  }

  /**
   * Completes the pending booking as if the payment had succeeded.
   *
   * @param session The HTTP session holding the pending booking.
   * @param redirectAttributes Flash attributes for the redirect.
   * @return Redirect to the invoices page.
   */
  @PostMapping("/dev/simulate-payment-success")
  public ModelAndView simulatePaymentSuccess(HttpSession session,
      RedirectAttributes redirectAttributes) {

    PendingBookingSessionRequest pending =
        (PendingBookingSessionRequest) session.getAttribute("PENDING_BOOKING");
    if (pending == null) {
      return new ModelAndView("redirect:/bookings");
    }

    Invoice invoice;
    try {
      invoice = checkoutService.completePaidBooking(pending);
    } catch (IllegalStateException ex) {
      redirectAttributes.addFlashAttribute("error", ex.getMessage());
      return new ModelAndView("redirect:/checkout");
    }

    session.removeAttribute("PENDING_BOOKING");
    shoppingCart.clear();

    redirectAttributes.addFlashAttribute("success",
        "[DEV] Simulated payment succeeded. Invoice " + invoice.getInvoiceNumber() + " generated.");
    return new ModelAndView("redirect:/users/invoices");
  }
}
