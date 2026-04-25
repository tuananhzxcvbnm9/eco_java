package com.eco.auth;

import com.eco.security.JwtService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final JwtService jwtService;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    encoder.encode(req.password());
    return ResponseEntity.ok(Map.of("email", req.email(), "status", "REGISTERED"));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    String token = jwtService.generateAccessToken("user-uuid", req.email(), "ROLE_USER");
    return ResponseEntity.ok(Map.of("accessToken", token, "tokenType", "Bearer", "expiresIn", 900));
  }

  public record RegisterRequest(@Email String email, @NotBlank @Size(min = 8) String password) {}
  public record LoginRequest(@Email String email, @NotBlank String password) {}
}
