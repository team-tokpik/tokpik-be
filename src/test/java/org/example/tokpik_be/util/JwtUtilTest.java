package org.example.tokpik_be.util;

import static org.assertj.core.api.Assertions.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private static String JWT_SECRET;
    private final long USER_ID = 1L;

    private JwtUtil jwtUtil;

    @BeforeAll
    static void generateJwtSecret() {
        byte[] bytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        JWT_SECRET = Base64.getEncoder().encodeToString(bytes);
    }

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(JWT_SECRET);
    }

    @DisplayName("access token 생성에 성공한다.")
    @Test
    void generateAccessToken() {
        // given
        Date generatedAt = Date.from(Instant.now());

        // when
        String accessToken = jwtUtil.generateAccessToken(USER_ID, generatedAt);

        // then
        assertThat(accessToken).isNotNull();
    }

    @DisplayName("refresh token 생성에 성공한다.")
    @Test
    void generateRefreshToken() {
        // given
        Date generatedAt = Date.from(Instant.now());

        // when
        String refreshToken = jwtUtil.generateRefreshToken(USER_ID, generatedAt);

        // then
        assertThat(refreshToken).isNotNull();
    }

    @Nested
    @DisplayName("JWT에서 userId 파싱 시 ")
    class ParseUserIdFromJwtTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            Date generatedAt = Date.from(Instant.now());
            String jwt = jwtUtil.generateAccessToken(USER_ID, generatedAt);

            // when
            long userId = jwtUtil.parseUserIdFromToken(jwt);

            // then
            assertThat(userId).isEqualTo(USER_ID);
        }

        @DisplayName("유효하지 않은 JWT에서 userId를 파싱할 경우 예외가 발생한다.")
        @Test
        void invalidJwt() {
            // given
            long millis = Duration.ofDays(30).toMillis();
            Date generatedAt = Date.from(Instant.now().minusMillis(millis));

            String jwt = jwtUtil.generateAccessToken(USER_ID, generatedAt);

            // when

            // then
            assertThatThrownBy(() -> jwtUtil.parseUserIdFromToken(jwt))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(LoginException.INVALID_JWT);
        }
    }
}
