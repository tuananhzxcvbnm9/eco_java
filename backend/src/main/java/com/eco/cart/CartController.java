package com.eco.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

  @PostMapping("/items")
  public ResponseEntity<?> addItem(@RequestBody AddCartItemRequest req) {
    return ResponseEntity.ok(Map.of("variantId", req.variantId(), "quantity", req.quantity(), "message", "ADDED"));
  }

  @PatchMapping("/items/{itemId}")
  public ResponseEntity<?> updateQty(@PathVariable UUID itemId, @RequestBody UpdateCartItemRequest req) {
    return ResponseEntity.ok(Map.of("itemId", itemId, "quantity", req.quantity(), "message", "UPDATED"));
  }

  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<?> remove(@PathVariable UUID itemId) {
    return ResponseEntity.noContent().build();
  }

  public record AddCartItemRequest(@NotNull UUID variantId, @Min(1) int quantity) {}
  public record UpdateCartItemRequest(@Min(1) int quantity) {}
}
