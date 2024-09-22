package org.example.tokpik_be.util.llm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicSearchResponse;

public record LLMTalkTopicsResponse(
    @JsonProperty(required = true)
    List<TalkTopicSearchResponse> responses
) {

}
