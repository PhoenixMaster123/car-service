package springboot.bg.harisauto.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
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
}