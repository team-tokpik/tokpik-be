package org.example.tokpik_be.login.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
    @Schema(type = "boolean", description = "프로필 생성 필요 여부, true일 경우 필요", example = "true")
    boolean requiresProfile,

    @Schema(type = "string", description = "access token", example = "header.payload.signature")
    String accessToken,

    @Schema(type = "string", description = "refresh token", example = "header.payload.signature")
    String refreshToken
) {

}

