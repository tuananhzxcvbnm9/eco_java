package com.eco.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String jwtSecret;

  @Value("${security.jwt.access-ttl-seconds:900}")
  private long accessTtl;

  public String generateAccessToken(String userId, String email, String role) {
    Instant now = Instant.now();

    return Jwts.builder()
        .subject(userId)
        .claims(Map.of("email", email, "role", role))
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(accessTtl)))
        .signWith(signingKey())
        .compact();
  }

  public Optional<Claims> parseClaims(String token) {
    try {
      Jws<Claims> jws = Jwts.parser()
          .verifyWith(signingKey())
          .build()
          .parseSignedClaims(token);
      return Optional.of(jws.getPayload());
    } catch (JwtException | IllegalArgumentException ex) {
      return Optional.empty();
    }
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
}
