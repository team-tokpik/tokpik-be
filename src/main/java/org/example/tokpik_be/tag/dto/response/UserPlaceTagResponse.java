package org.example.tokpik_be.tag.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserPlaceTagResponse", description = "사용자 장소 태그 응답")
public record UserPlaceTagResponse(
    @Schema(type="number", description = "사용자 ID", example = "1")
    Long userId,

    @Schema(type = "array", description = "장소 태그 목록")
    List<UserPlaceTagResponse.PlaceTagDTO> placeTopicTags

) {
    @Schema(name = "PlaceTagDTO", description = "장소 태그 정보")
    public record PlaceTagDTO(
        @Schema(type="number", description = "장소 태그 ID", example = "1")
        Long id,

        @Schema(type = "string", description = "장소 태그 내용", example = "집")
        String content
    ) {

    }
}
