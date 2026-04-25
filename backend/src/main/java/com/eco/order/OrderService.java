package com.eco.order;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  @Transactional
  public Map<String, Object> createOrder(String userId, String idempotencyKey) {
    // 1) validate cart + inventory
    // 2) reserve inventory
    // 3) create order + order items
    // 4) emit ORDER_CREATED event
    return Map.of(
        "orderId", UUID.randomUUID(),
        "orderNo", "EC" + System.currentTimeMillis(),
        "status", "PENDING_PAYMENT",
        "idempotencyKey", idempotencyKey,
        "userId", userId);
  }
}
