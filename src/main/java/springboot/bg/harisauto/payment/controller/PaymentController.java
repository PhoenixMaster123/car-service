package springboot.bg.harisauto.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springboot.bg.harisauto.payment.client.PaymentClient;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;

/**
 * PaymentController acts as a proxy to the actual payment-gateway service.
 *
 * @author Kristian Popov
 */
@RestController
public class PaymentController {

  private final PaymentClient client;

  @Autowired
  public PaymentController(PaymentClient client) {
    this.client = client;
  }

  /**
   * This endpoint is called by the checkout.html JavaScript.
   * It uses Feign to call the *actual* payment-gateway app.
   */
  @PostMapping("/payment/api/create-payment-intent")
  public PaymentResponse proxyCreatePaymentIntent(@RequestBody PaymentRequest request) {
    return client.createPaymentIntent(request);
  }
}