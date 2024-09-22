package org.example.tokpik_be.talk_topic.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record TalkTopicSearchResponse(
    @JsonProperty(required = true)
    @Schema(type = "string", description = "제목", example = "MBTI/T발 너 C야?")
    String title,

    @JsonProperty(required = true)
    @Schema(type = "string", description = "부제목", example = "MBTI, 확실히 알려드릴게요")
    String subtitle,

    @JsonProperty(required = true)
    @Schema(type = "string", description = "대화태그", example = "요즘이슈")
    String topicTag
) {

}
