package org.example.tokpik_be.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${JWT_SECRET}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(5);
    private final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(3);

    public String generateAccessToken(long userId) {

        return generateUserJwt(userId, ACCESS_TOKEN_DURATION);
    }

    public String generateRefreshToken(long userId) {

        return generateUserJwt(userId, REFRESH_TOKEN_DURATION);
    }

    private String generateUserJwt(long userId, Duration duration) {

        return Jwts.builder()
            .signWith(key)
            .claim("userId", userId)
            .expiration(new Date(duration.toMillis()))
            .compact();
    }
}
