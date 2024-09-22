package org.example.tokpik_be.talk_topic.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicSearchResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalkTopicQueryService {

    private final UserQueryService userQueryService;
    private final LLMApiClient llmApiClient;

    public List<TalkTopicSearchResponse> searchTalkTopics(long userId,
        TalkTopicSearchRequest request) {

        User user = userQueryService.findById(userId);
        LLMTalkTopicSearchRequest llmTalkTopicSearchRequest;

        if (request.includeFilterCondition()) {
            llmTalkTopicSearchRequest = LLMTalkTopicSearchRequest.from(request);
        } else {
            List<TopicTag> topicTags = user.getUserTopicTags().stream()
                .map(UserTopicTag::getTopicTag).toList();
            List<PlaceTag> placeTags = user.getUserPlaceTags().stream()
                .map(UserPlaceTag::getPlaceTag).toList();
            llmTalkTopicSearchRequest = LLMTalkTopicSearchRequest.from(topicTags, placeTags);
        }

        return llmApiClient.searchTalkTopics(llmTalkTopicSearchRequest);
    }
}
