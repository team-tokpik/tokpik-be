package org.example.tokpik_be.scrap.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScrapCountResponse(

    @JsonProperty(required = true)
    @Schema(description = "갯수")
    Long count
) {

}
