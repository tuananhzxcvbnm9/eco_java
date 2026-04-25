package com.eco.payment;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/webhooks")
public class PaymentWebhookController {

  @Value("${payment.webhook.secret:change-me}")
  private String secret;

  @PostMapping("/stripe")
  public ResponseEntity<?> handleStripe(@RequestBody String payload, HttpServletRequest request) throws Exception {
    String signature = request.getHeader("X-Signature");
    if (!verify(payload, signature)) {
      return ResponseEntity.status(401).body(Map.of("code", "INVALID_SIGNATURE"));
    }

    // idempotent update payment/order status
    return ResponseEntity.ok(Map.of("received", true));
  }

  private boolean verify(String payload, String signature) throws Exception {
    Mac hmac = Mac.getInstance("HmacSHA256");
    hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    String digest = bytesToHex(hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    return digest.equalsIgnoreCase(signature);
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) sb.append(String.format("%02x", b));
    return sb.toString();
  }
}
