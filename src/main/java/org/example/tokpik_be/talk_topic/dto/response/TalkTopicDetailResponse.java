package org.example.tokpik_be.talk_topic.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record TalkTopicDetailResponse(
    @Schema(type = "array", description = "상세 내용 항목들")
    List<TalkTopicDetailItemResponse> details,

    @Schema(type = "boolean", description = "스크랩 여부", example = "false")
    boolean scraped
) {

    public record TalkTopicDetailItemResponse(
        @Schema(type = "string", description = "항목 제목", example = "개인적인 고민을 유도하며 공감")
        String itemTitle,

        @Schema(type = "string", description = "항목 내용", example = "상대방이 고민을 얘기하면...")
        String itemContent
    ) {

    }
}
