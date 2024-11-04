package org.example.tokpik_be.scrap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.scrap.repository.ScrapTopicRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScrapServiceTest {

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private ScrapTopicRepository scrapTopicRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private TalkTopicQueryService talkTopicQueryService;

    @InjectMocks
    private ScrapService scrapService;

    @DisplayName("사용자는 스크랩을 생성할 수 있다.")
    @Test
    void createScrap() {
        // given
        long userId = 1L;
        User user = new User("ex@example.com", "https://www.example.com/profile-photo");
        given(userQueryService.findById(userId)).willReturn(user);

        long scrapId = 1L;
        Scrap savedScrap = mock(Scrap.class);
        given(savedScrap.getId()).willReturn(scrapId);
        given(scrapRepository.save(any(Scrap.class))).willReturn(savedScrap);

        ScrapCreateRequest request = new ScrapCreateRequest("스크랩 이름");

        // when
        ScrapCreateResponse response = scrapService.createScrap(userId, request);

        // then
        assertThat(response.scrapId()).isEqualTo(scrapId);
    }

    @DisplayName("사용자는 대화 주제를 스크랩할 수 있다.")
    @Test
    void scrapTopic() {
        // given
        long scrapId = 1L;
        Scrap scrap = mock(Scrap.class);
        given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

        long topicId = 1L;
        TalkTopic talkTopic = mock(TalkTopic.class);
        given(talkTopicQueryService.findById(topicId)).willReturn(talkTopic);

        // when
        scrapService.scrapTopic(scrapId, topicId);

        // then
        verify(scrapTopicRepository).save(any(ScrapTopic.class));
    }
}
