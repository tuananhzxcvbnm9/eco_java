package com.eco.checkout;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

  public Map<String, Object> preview(String couponCode, BigDecimal subtotal, BigDecimal shippingFee) {
    BigDecimal discount = (couponCode != null && !couponCode.isBlank()) ? subtotal.multiply(new BigDecimal("0.10")) : BigDecimal.ZERO;
    BigDecimal grandTotal = subtotal.subtract(discount).add(shippingFee);

    return Map.of(
        "subtotal", subtotal,
        "discount", discount,
        "shippingFee", shippingFee,
        "grandTotal", grandTotal,
        "couponCode", couponCode);
  }
}
