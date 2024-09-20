package org.example.tokpik_be.login.dto.response;

public record AccessTokenRefreshResponse(
    String accessToken,
    String refreshToken
) {

}
