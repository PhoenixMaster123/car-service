package springboot.bg.harisauto.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.dto.response.PaymentIntentResponse;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;

/**
 * Feign client for interacting with the Payment Service.
 *
 * @author Kristian Popov
 */
@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentClient {

  /**
   * Create a new Payment Intent.
   *
   * @param request PaymentRequest
   * @return PaymentResponse
   */
  @PostMapping("/api/payment/create")
  PaymentResponse createPaymentIntent(@RequestBody PaymentRequest request);

  /**
   * Reads a payment intent back from the payment service.
   *
   * <p>This is the only trustworthy statement about whether money moved. The
   * {@code redirect_status} on the browser return URL is supplied by the user agent
   * and must never be used to decide that a payment succeeded.</p>
   *
   * @param paymentIntentId The intent id returned when the intent was created.
   * @return The intent as the payment provider sees it.
   */
  @GetMapping("/api/payment/{paymentIntentId}")
  PaymentIntentResponse getPaymentIntent(@PathVariable("paymentIntentId") String paymentIntentId);
}