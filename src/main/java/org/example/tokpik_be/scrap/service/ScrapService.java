package org.example.tokpik_be.scrap.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.request.ScrapUpdateTitleRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCountResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapResponse;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.scrap.repository.ScrapTopicRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public ScrapCountResponse getUserSrcapCounts(long userId) {

        User user = userQueryService.findById(userId);

        Long count = scrapRepository.countByUser(user);

        return new ScrapCountResponse(count);
    }

    public ScrapCountResponse getUserTopicCounts(long userId) {

        User user = userQueryService.findById(userId);

        Long count = scrapTopicRepository.countByUserId(userId);

        return new ScrapCountResponse(count);
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

    @Transactional
    public void deleteScrap(long scrapId) {
        Scrap scrap = findById(scrapId);
        scrapRepository.delete(scrap);
    }

    @Transactional
    public void deleteScrapTopic(long scrapId, long scrapTopicId) {
        Scrap scrap = findById(scrapId);
        ScrapTopic scrapTopic = findByScrapTopicId(scrapTopicId);

        if (!scrap.getScrapTopics().contains(scrapTopic)) {
            throw new GeneralException(ScrapException.INVALID_SCRAP_TOPIC);
        }

        scrapTopicRepository.delete(scrapTopic);
    }

    private ScrapTopic findByScrapTopicId(long scrapTopicId) {
        return scrapTopicRepository.findById(scrapTopicId)
            .orElseThrow(() -> new GeneralException(ScrapException.SCRAP_TOPIC_NOT_FOUND));

    }

    public ScrapResponse getScrapTopics(Long scrapId, Long lastCursorId, int size) {

        Scrap scrap = findById(scrapId);

        if (lastCursorId != null && lastCursorId > 0) {
            boolean isValidLastCursor = scrapTopicRepository.existsByScrapIdAndId(scrapId,
                lastCursorId);
            if (!isValidLastCursor) {
                throw new GeneralException(ScrapException.INVALID_SCRAP_TOPIC);
            }
        }

        Pageable pageable = PageRequest.of(0, size);
        List<ScrapTopic> scrapTopics = scrapTopicRepository
            .findByScrapIdAndIdGreaterThanOrderByIdAsc(scrapId, lastCursorId, pageable);

        List<ScrapResponse.ScrapTopicResponse> contents = scrapTopics.stream()
            .map(scrapTopic -> {
                TalkTopic talkTopic = scrapTopic.getTalkTopic();
                boolean isScraped = isTopicScraped(scrapId, talkTopic.getId());
                return new ScrapResponse.ScrapTopicResponse(
                    scrapTopic.getId(),
                    talkTopic.getId(),
                    talkTopic.getTitle(),
                    talkTopic.getTopicTag().getContent(),
                    isScraped
                );
            })
            .toList();

        Long newLastCursorId = contents.isEmpty() ? lastCursorId :
            scrapTopics.get(scrapTopics.size() - 1).getId();

        boolean isFirst;
        if (lastCursorId == null || lastCursorId == 0) {
            isFirst = true;
        } else {
            long countAfterLastCursor = scrapTopicRepository
                .countByScrapIdAndIdGreaterThan(scrapId, lastCursorId);
            isFirst = countAfterLastCursor == 0;
        }

        boolean isLast = scrapTopics.size() < size;

        return new ScrapResponse(
            contents,
            newLastCursorId,
            isFirst,
            isLast
        );
    }

    private boolean isTopicScraped(Long scrapId, Long topicId) {
        return scrapRepository.existsByIdAndScrapTopicsTalkTopicId(scrapId, topicId);
    }

    @Transactional
    public void updateScrapTitle(long userId, long scrapId, ScrapUpdateTitleRequest request) {
        User user = userQueryService.findById(userId);
        Scrap scrap = findById(scrapId);

        if (!scrap.getUser().equals(user)) {
            throw new GeneralException(ScrapException.UNAUTHORIZED_SCRAP_ACCESS);
        }

        scrap.updateTitle(request.scrapTitle());
    }
}
