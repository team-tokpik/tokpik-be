package org.example.tokpik_be.talk_topic.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TalkTopicException;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TalkTopicQueryServiceTest {

    @Mock
    private TalkTopicRepository talkTopicRepository;

    @InjectMocks
    private TalkTopicQueryService talkTopicQueryService;

    @Nested
    @DisplayName("Id로 대화 주제 조회 시 ")
    class FindByIdTest {
        private final long TOPIC_ID = 1L;

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            TalkTopic talkTopic = Mockito.mock(TalkTopic.class);
            given(talkTopicRepository.findById(TOPIC_ID)).willReturn(Optional.of(talkTopic));

            // when
            TalkTopic result = talkTopicQueryService.findById(TOPIC_ID);

            // then
            assertThat(result).isEqualTo(result);
        }

        @DisplayName("존재하지 않는 대화 주제일 경우 예외가 발생한다.")
        @Test
        void talkTopicNotFound() {
            // given
            given(talkTopicRepository.findById(TOPIC_ID)).willReturn(Optional.empty());

            // when

            // then
            assertThatThrownBy(() -> talkTopicQueryService.findById(TOPIC_ID))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TalkTopicException.TALK_TOPIC_NOT_FOUND);
        }
    }
}
