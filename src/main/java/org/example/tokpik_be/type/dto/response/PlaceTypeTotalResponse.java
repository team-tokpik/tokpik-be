package org.example.tokpik_be.type.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.example.tokpik_be.type.domain.PlaceType;

public record PlaceTypeTotalResponse(
    @Schema(type = "array", description = "대화 장소들")
    List<PlaceTypeResponse> placeTypes
) {

    public record PlaceTypeResponse(
        @Schema(type = "number", description = "대화 장소 ID", example = "1")
        long placeTypeId,

        @Schema(type = "string", description = "대화 장소 내용", example = "학교")
        String content
    ) {

        public static PlaceTypeResponse from(PlaceType placeType) {

            return new PlaceTypeResponse(placeType.getId(), placeType.getContent());
        }
    }
}
