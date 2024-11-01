package org.example.tokpik_be.talk_topic.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.domain.UserPlaceType;
import org.example.tokpik_be.type.domain.UserTopicType;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse.TalkTopicSearchResponse;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse.LLMTalkTopicResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TalkTopicCommandService {

    private final TopicTypeRepository topicTypeRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final TalkTopicRepository talkTopicRepository;
    private final UserQueryService userQueryService;
    private final LLMApiClient llmApiClient;

    public TalkTopicsSearchResponse generateTopics(long userId, TalkTopicSearchRequest request) {
        List<TopicType> topicTypes = topicTypeRepository.findAll();
        List<PlaceType> placeTypes = placeTypeRepository.findAll();
        User user = userQueryService.findById(userId);

        LLMTalkTopicSearchRequest llmRequest = createLLMRequest(request, user, topicTypes,
            placeTypes);
        LLMTalkTopicsResponse llmResponse = llmApiClient.searchTalkTopics(llmRequest);

        List<TalkTopic> talkTopics = createTalkTopics(llmResponse, topicTypes, placeTypes);
        talkTopicRepository.saveAll(talkTopics);

        return new TalkTopicsSearchResponse(talkTopics.stream()
            .map(talkTopic -> TalkTopicSearchResponse.from(talkTopic, false))
            .toList());
    }

    private LLMTalkTopicSearchRequest createLLMRequest(TalkTopicSearchRequest request,
        User user,
        List<TopicType> topicTypes,
        List<PlaceType> placeTypes) {
        if (request.includeFilterCondition()) {

            return LLMTalkTopicSearchRequest.from(topicTypes, placeTypes, request);
        }

        List<TopicType> userTopicTypes = user.getUserTopicTypes().stream()
            .map(UserTopicType::getTopicType)
            .toList();
        List<PlaceType> userPlaceTypes = user.getUserPlaceTypes().stream()
            .map(UserPlaceType::getPlaceType)
            .toList();

        return LLMTalkTopicSearchRequest.from(userTopicTypes, userPlaceTypes);
    }

    private List<TalkTopic> createTalkTopics(LLMTalkTopicsResponse llmResponse,
        List<TopicType> topicTypes,
        List<PlaceType> placeTypes) {
        Map<String, TopicType> topicTypeMap = createTypeMap(topicTypes, TopicType::getContent);
        Map<String, PlaceType> placeTypeMap = createTypeMap(placeTypes, PlaceType::getContent);

        return llmResponse.responses().stream()
            .map(response -> createTalkTopic(response, topicTypeMap, placeTypeMap))
            .toList();
    }

    private <T> Map<String, T> createTypeMap(List<T> types, Function<T, String> keyExtractor) {

        return types.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    private TalkTopic createTalkTopic(LLMTalkTopicResponse response,
        Map<String, TopicType> topicTypeMap,
        Map<String, PlaceType> placeTypeMap) {
        TopicType topicType = Optional.ofNullable(topicTypeMap.get(response.topicType()))
            .orElseThrow(() -> new GeneralException(TypeException.TYPE_NOT_FOUND));
        PlaceType placeType = Optional.ofNullable(placeTypeMap.get(response.placeType()))
            .orElseThrow(() -> new GeneralException(TypeException.TYPE_NOT_FOUND));

        TalkPartner talkPartner = new TalkPartner(
            Gender.from(response.talkPartnerGender()),
            response.talkPartnerAgeLowerBound(),
            response.talkPartnerAgeUpperBound()
        );

        return new TalkTopic(response.title(),
            response.subTitle(),
            response.situation(),
            talkPartner,
            topicType,
            placeType);
    }
}
