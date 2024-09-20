package org.example.tokpik_be.tag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
            User user = new User("test@naver.com", "url", "kakaoId", "token", LocalDateTime.now(), Gender.MALE);
            user.setId(userId);

            UserTopicTag userTopicTag = new UserTopicTag();
            userTopicTag.setTopicTag(new TopicTag(1L, "Tag 1"));

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userTopicTagRepository.findByUserId(userId)).willReturn(List.of(userTopicTag));

            UserTopicTagResponse response = topicTagService.getUserTopicTags(userId);

            assertEquals(userId, response.userId());
            assertEquals("Tag 1", response.talkTopicTags().get(0).content());
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
