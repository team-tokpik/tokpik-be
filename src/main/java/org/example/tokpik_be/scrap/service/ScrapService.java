package org.example.tokpik_be.scrap.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.scrap.repository.ScrapTopicRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final ScrapTopicRepository scrapTopicRepository;
    private final UserQueryService userQueryService;
    private final TalkTopicQueryService talkTopicQueryService;

    public ScrapListResponse getScrapList(long userId) {

        User user = userQueryService.findById(userId);

        List<Scrap> scraps = scrapRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<ScrapListResponse.ScrapResponse> scrapResponses = scraps.stream()
            .map(this::mapToScrapResponse)
            .toList();

        return new ScrapListResponse(scrapResponses);
    }

    private ScrapListResponse.ScrapResponse mapToScrapResponse(Scrap scrap) {
        List<ScrapTopic> scrapTopics = scrapTopicRepository.findByScrapOrderByCreatedAtDesc(scrap);

        List<ScrapListResponse.TopicTypeResponse> topicTypes = scrapTopics.stream()
            .map(this::mapToTopicTypeResponse)
            .toList();

        return new ScrapListResponse.ScrapResponse(
            scrap.getId(),
            scrap.getTitle(),
            topicTypes
        );
    }

    private ScrapListResponse.TopicTypeResponse mapToTopicTypeResponse(ScrapTopic scrapTopic) {
        TalkTopic talkTopic = scrapTopic.getTalkTopic();
        return new ScrapListResponse.TopicTypeResponse(
            talkTopic.getTopicTag().getId(),
            talkTopic.getTopicTag().getContent()
        );
    }

    @Transactional
    public ScrapCreateResponse createScrap(long userId, ScrapCreateRequest request) {
        User user = userQueryService.findById(userId);

        Scrap scrap = new Scrap(request.scrapName(), user);
        scrapRepository.save(scrap);

        return new ScrapCreateResponse(scrap.getId());
    }

    @Transactional
    public void scrapTopic(long scrapId, long topicId) {
        Scrap scrap = findById(scrapId);
        TalkTopic talkTopic = talkTopicQueryService.findById(topicId);

        ScrapTopic scrapTopic = new ScrapTopic(scrap, talkTopic);
        scrapTopicRepository.save(scrapTopic);
    }

    private Scrap findById(long scrapId) {

        return scrapRepository.findById(scrapId)
            .orElseThrow(() -> new GeneralException(ScrapException.SCRAP_NOT_FOUND));
    }
}
