package org.example.tokpik_be.util.llm.client;

import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse;

public interface LLMApiClient {

    LLMTalkTopicsResponse searchTalkTopics(LLMTalkTopicSearchRequest request);
}
