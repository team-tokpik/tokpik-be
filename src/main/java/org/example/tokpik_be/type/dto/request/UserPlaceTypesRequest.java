package org.example.tokpik_be.type.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserPlaceTypesRequest", description = "사용자 장소 타입 요청")
public record UserPlaceTypesRequest(

    @Schema(type = "array", description = "장소 타입 ID 목록", example = "[1, 2, 3]")
    List<Long> placeTypeIds
) {
}
