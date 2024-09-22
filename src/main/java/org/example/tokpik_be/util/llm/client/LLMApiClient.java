package org.example.tokpik_be.util.llm.client;

import java.util.List;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicSearchResponse;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;

public interface LLMApiClient {

    List<TalkTopicSearchResponse> searchTalkTopics(LLMTalkTopicSearchRequest request);
}
