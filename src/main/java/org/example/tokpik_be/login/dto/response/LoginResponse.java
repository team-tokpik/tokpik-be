package org.example.tokpik_be.login.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
    @Schema(type = "string", description = "access token", example = "header.payload.signature")
    String accessToken,
    @Schema(type = "string", description = "refresh token", example = "header.payload.signature")
    String refreshToken
) {

}
