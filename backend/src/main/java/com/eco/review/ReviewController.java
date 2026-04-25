package com.eco.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

  @PostMapping
  public ResponseEntity<?> create(@RequestBody ReviewRequest req) {
    // validate purchased order item and delivered status
    return ResponseEntity.ok(Map.of("id", UUID.randomUUID(), "productId", req.productId(), "rating", req.rating()));
  }

  public record ReviewRequest(@NotNull UUID productId, @NotNull UUID orderItemId, @Min(1) @Max(5) int rating, @NotBlank String content) {}
}
