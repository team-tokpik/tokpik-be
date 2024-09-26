package org.example.tokpik_be.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.example.tokpik_be.tag.domain.PlaceTag;

public record PlaceTagTotalResponse(
    @Schema(type = "array", description = "대화 장소들")
    List<PlaceTagResponse> placeTags
) {

    public record PlaceTagResponse(
        @Schema(type = "number", description = "대화 장소 ID", example = "1")
        long placeTagId,

        @Schema(type = "string", description = "대화 장소 내용", example = "학교")
        String content
    ) {

        public static PlaceTagResponse from(PlaceTag placeTag) {

            return new PlaceTagResponse(placeTag.getId(), placeTag.getContent());
        }
    }
}
