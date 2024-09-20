package org.example.tokpik_be.tag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.example.tokpik_be.tag.service.TopicTagService;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TopicTagServiceTest {

    @Mock
    private UserTopicTagRepository userTopicTagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TopicTagService topicTagService;

    @Nested
    @DisplayName("사용자 대화 태그 조회 시")
    class GetUserTopicTagsTest {

        private final long userId = 1L;

        @Test
        @DisplayName("성공한다.")
        void success() {
            User user = Mockito.mock(User.class);
            given(user.getId()).willReturn(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            UserTopicTag userTopicTag = new UserTopicTag();
            userTopicTag.setTopicTag(new TopicTag(1L, "Tag 1"));

            given(userTopicTagRepository.findByUserId(userId)).willReturn(List.of(userTopicTag));

            UserTopicTagResponse response = topicTagService.getUserTopicTags(userId);

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(response.userId()).isEqualTo(userId);
                softly.assertThat(response.talkTopicTags().get(0).content()).isEqualTo("Tag 1");
            });
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() {

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            GeneralException exception = assertThrows(GeneralException.class, () -> topicTagService.getUserTopicTags(userId));
            assertEquals(UserException.USER_NOT_FOUND.getMessage(), exception.getMessage());
        }
    }

}
