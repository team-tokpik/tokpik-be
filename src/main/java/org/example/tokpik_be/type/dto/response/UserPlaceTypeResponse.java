package org.example.tokpik_be.type.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserPlaceTypeResponse", description = "사용자 장소 태그 응답")
public record UserPlaceTypeResponse(
    @Schema(type="number", description = "사용자 ID", example = "1")
    Long userId,

    @Schema(type = "array", description = "장소 태그 목록")
    List<UserPlaceTypeResponse.PlaceTypeDTO> placeTopicTypes

) {
    @Schema(name = "PlaceTypeDTO", description = "장소 태그 정보")
    public record PlaceTypeDTO(
        @Schema(type="number", description = "장소 태그 ID", example = "1")
        Long id,

        @Schema(type = "string", description = "장소 태그 내용", example = "집")
        String content
    ) {

    }
}
