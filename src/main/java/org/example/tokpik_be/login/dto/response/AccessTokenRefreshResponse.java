package org.example.tokpik_be.login.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AccessTokenRefreshResponse(
    @Schema(type = "string", description = "새로운 access token", example = "header.payload.signature")
    String accessToken
) {

}
