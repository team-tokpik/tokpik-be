package org.example.tokpik_be.login.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginByKakaoRequest(
    @Schema(type = "string", description = "카카오 서버로부터 받은 인가 코드", example = "code")
    @NotBlank(message = "인가 코드는 필수 값")
    String code
) {

}
