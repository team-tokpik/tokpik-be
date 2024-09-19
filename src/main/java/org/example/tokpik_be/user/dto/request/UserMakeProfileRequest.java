package org.example.tokpik_be.user.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.example.tokpik_be.user.deserializer.GenderDeserializer;
import org.example.tokpik_be.user.enums.Gender;

public record UserMakeProfileRequest(
    @Schema(type = "string", description = "생년월일", example = "1998-04-08")
    @NotNull(message = "생년월일은 필수값")
    LocalDate birth,

    @Schema(type = "boolean", description = "성별(남=true, 여=false)", example = "true")
    @JsonDeserialize(using = GenderDeserializer.class)
    Gender gender
) {

}
