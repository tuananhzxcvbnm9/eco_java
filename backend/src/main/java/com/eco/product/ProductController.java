package com.eco.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  @GetMapping
  public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(Map.of("page", PageRequest.of(page, size), "items", List.of()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> detail(@PathVariable UUID id) {
    return ResponseEntity.ok(Map.of("id", id, "name", "Demo Product"));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
  public ResponseEntity<?> create(@Valid @RequestBody ProductUpsertRequest req) {
    return ResponseEntity.ok(Map.of("id", UUID.randomUUID(), "name", req.name(), "price", req.price()));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
  public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ProductUpsertRequest req) {
    return ResponseEntity.ok(Map.of("id", id, "name", req.name(), "price", req.price()));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
  public ResponseEntity<?> softDelete(@PathVariable UUID id) {
    return ResponseEntity.noContent().build();
  }

  public record ProductUpsertRequest(@NotBlank String name, @NotNull BigDecimal price, @NotNull UUID categoryId) {}
}
