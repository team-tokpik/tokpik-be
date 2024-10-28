package org.example.tokpik_be.type.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserTopicTypesRequest", description = "사용자 대화 타입 요청")
public record UserTopicTypesRequest(

    @Schema(type = "array", description = "대화 타입 ID 목록", example = "[1, 2, 3]")
    List<Long> topicTypeIds
) {
}
