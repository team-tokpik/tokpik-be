package org.example.tokpik_be.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record UserMakeProfileRequest(
    @Schema(type = "string", description = "생년월일", example = "1998-04-08")
    @NotNull(message = "생년월일은 필수값")
    @PastOrPresent(message = "생년월일은 과거,현재만 가능")
    LocalDate birth,

    @Schema(type = "boolean", description = "성별(남=true, 여=false)", example = "true")
    boolean gender
) {

}
