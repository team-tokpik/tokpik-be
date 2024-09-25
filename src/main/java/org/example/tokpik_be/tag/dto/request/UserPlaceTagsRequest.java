package org.example.tokpik_be.tag.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserPlaceTagsRequest", description = "사용자 장소 태그 요청")
public record UserPlaceTagsRequest(

    @Schema(type = "array", description = "장소 태그 ID 목록", example = "[1, 2, 3]")
    List<Long> placeTagIds
) {
}
