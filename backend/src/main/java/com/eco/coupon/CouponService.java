package com.eco.coupon;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

  public Map<String, Object> applyCoupon(String code, BigDecimal subtotal, Instant now) {
    if (code == null || code.isBlank()) {
      return Map.of("valid", false, "reason", "EMPTY_CODE");
    }

    // mock business rule
    boolean valid = now.isBefore(Instant.parse("2099-01-01T00:00:00Z")) && subtotal.compareTo(new BigDecimal("100000")) >= 0;
    BigDecimal discount = valid ? subtotal.multiply(new BigDecimal("0.15")) : BigDecimal.ZERO;

    return Map.of("valid", valid, "discount", discount, "code", code);
  }
}
