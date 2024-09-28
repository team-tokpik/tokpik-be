package org.example.tokpik_be.util.llm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record LLMTalkTopicDetailResponse(
    @JsonProperty(required = true)
    List<LLMTalkTopicDetailItemResponse> details
) {

    public record LLMTalkTopicDetailItemResponse(
        @JsonProperty(required = true)
        String itemTitle,

        @JsonProperty(required = true)
        String itemContent
    ) {

    }
}
