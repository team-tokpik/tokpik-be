package org.example.tokpik_be.login.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AccessTokenRefreshRequest(
    @Schema(type = "string", description = "refresh token", example = "header.payload.signature")
    @NotBlank(message = "refresh token은 필수값")
    String refreshToken
) {

}
