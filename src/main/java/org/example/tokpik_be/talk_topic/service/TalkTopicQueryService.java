package org.example.tokpik_be.talk_topic.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TalkTopicException;
import org.example.tokpik_be.scrap.repository.QueryDslScrapRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicDetailResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicDetailResponse.TalkTopicDetailItemResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse;
import org.example.tokpik_be.talk_topic.repository.QueryDslTalkTopicRepository;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicDetailRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicDetailResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalkTopicQueryService {

    private final TalkTopicRepository talkTopicRepository;
    private final QueryDslTalkTopicRepository queryDslTalkTopicRepository;
    private final QueryDslScrapRepository queryDslScrapRepository;

    private final UserQueryService userQueryService;
    private final LLMApiClient llmApiClient;

    public TalkTopic findById(long topicId) {

        return talkTopicRepository.findById(topicId)
            .orElseThrow(() -> new GeneralException(TalkTopicException.TALK_TOPIC_NOT_FOUND));
    }

    @Cacheable(value = "relatedTalkTopics", key = "#topicId")
    public TalkTopicsRelatedResponse getRelatedTopics(long userId, long topicId) {
        User user = userQueryService.findById(userId);
        TalkTopic baseTopic = findById(topicId);

        return queryDslTalkTopicRepository.findRelatedTalkTopics(user.getId(), baseTopic);
    }

    public TalkTopicDetailResponse getTalkTopicDetail(long userId, long topicId) {
        User user = userQueryService.findById(userId);
        TalkTopic talkTopic = findById(topicId);

        LLMTalkTopicDetailRequest request = LLMTalkTopicDetailRequest.from(talkTopic);
        LLMTalkTopicDetailResponse llmResponse = llmApiClient
            .generateTalkTopicDetail(request);

        List<TalkTopicDetailItemResponse> details = llmResponse.details().stream()
            .map(item -> new TalkTopicDetailItemResponse(item.itemTitle(), item.itemContent()))
            .toList();
        boolean scraped = queryDslScrapRepository.checkTopicScrapedBy(user, talkTopic);

        return new TalkTopicDetailResponse(details, scraped);
    }
}
