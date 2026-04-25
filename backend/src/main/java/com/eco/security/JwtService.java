package com.eco.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
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
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .subject(userId)
        .claims(Map.of("email", email, "role", role))
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(accessTtl)))
        .signWith(key)
        .compact();
  }
}
