package org.example.tokpik_be.tag.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.domain.UserTopicTag;
import org.example.tokpik_be.tag.dto.request.UserTopicTagsRequest;
import org.example.tokpik_be.tag.dto.response.TopicTagTotalResponse;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class TopicTagServiceTest extends ServiceTestSupport {

    private TopicTagService topicTagService;

    @MockBean
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        topicTagService = new TopicTagService(userTopicTagRepository, userQueryService, topicTagRepository);
    }

    @Nested
    @DisplayName("사용자 대화 태그 조회 시 ")
    class GetTopicTagsTest {

        @Test
        @DisplayName("성공한다.")
        void getMyTopicTags() {

            // Given
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicTag topicTag1 = new TopicTag("사랑과 연애");
            TopicTag topicTag2 = new TopicTag("비즈니스와 업무");
            topicTagRepository.saveAll(Arrays.asList(topicTag1, topicTag2));

            UserTopicTag userTopicTag1 = new UserTopicTag(userId, topicTag1);
            UserTopicTag userTopicTag2 = new UserTopicTag(userId, topicTag2);
            userTopicTagRepository.saveAll(Arrays.asList(userTopicTag1, userTopicTag2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            // When
            UserTopicTagResponse response = topicTagService.getUserTopicTags(userId);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.talkTopicTags())
                .hasSize(2)
                .map(UserTopicTagResponse.TopicTagDTO::content)
                .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무");

            assertThat(response.talkTopicTags())
                .map(UserTopicTagResponse.TopicTagDTO::id)
                .doesNotContainNull();
        }
    }

    @Nested
    @DisplayName("사용자 대화 태그 수정 시 ")
    class UpdateTopicTagsTest {
        @Test
        @DisplayName("성공한다.")
        void updateMyTopicTags() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicTag topicTag1 = new TopicTag("사랑과 연애");
            TopicTag topicTag2 = new TopicTag("비즈니스와 업무");
            TopicTag topicTag3 = new TopicTag("아이스브레이킹");
            topicTagRepository.saveAll(Arrays.asList(topicTag1, topicTag2, topicTag3));

            UserTopicTag userTopicTag1 = new UserTopicTag(userId, topicTag1);
            UserTopicTag userTopicTag2 = new UserTopicTag(userId, topicTag2);
            userTopicTagRepository.saveAll(Arrays.asList(userTopicTag1, userTopicTag2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> newTagIds = Arrays.asList(topicTag1.getId(), topicTag3.getId());
            UserTopicTagsRequest request = new UserTopicTagsRequest(newTagIds);

            // When
            UserTopicTagResponse response = topicTagService.updateUserTopicTags(userId, request);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.talkTopicTags())
                .hasSize(2)
                .allSatisfy(tags -> {
                    assertThat(tags.content()).isIn("사랑과 연애", "아이스브레이킹");
                    assertThat(tags.id()).isIn(topicTag1.getId(), topicTag3.getId());
                });
        }


        @Test
        @DisplayName("중복된 값을 요청 데이터에 포함하면 예외가 발생한다.")
        void duplicateTags() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicTag topicTag1 = new TopicTag("사랑과 연애");
            topicTagRepository.save(topicTag1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> duplicateTagIds = Arrays.asList(topicTag1.getId(), topicTag1.getId());
            UserTopicTagsRequest request = new UserTopicTagsRequest(duplicateTagIds);

            // Then
            assertThatThrownBy(() -> topicTagService.updateUserTopicTags(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TagException.DUPLICATE_TAGS.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 값이면 예외가 발생한다.")
        void tagNotFound() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicTag topicTag1 = new TopicTag("사랑과 연애");
            topicTagRepository.save(topicTag1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);


            List<Long> invalidTagIds = Arrays.asList(topicTag1.getId(), 10L);
            UserTopicTagsRequest request = new UserTopicTagsRequest(invalidTagIds);

            // Then
            assertThatThrownBy(() -> topicTagService.updateUserTopicTags(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TagException.TAG_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("대화 태그 전체 조회 시 ")
    class GetAllTopicTagsTest {

        @Test
        @DisplayName("성공한다.")
        void getAllTopicTags() {

            // Given
            TopicTag topicTag1 = new TopicTag("사랑과 연애");
            TopicTag topicTag2 = new TopicTag("비즈니스와 업무");
            TopicTag topicTag3 = new TopicTag("아이스브레이킹");
            topicTagRepository.saveAll(Arrays.asList(topicTag1, topicTag2, topicTag3));

            em.flush();
            em.clear();

            // When
            TopicTagTotalResponse result = topicTagService.getAllTopicTags();

            // Then
            assertThat(result.topicTags())
                .hasSize(3)
                .satisfies(tags -> {
                    assertThat(tags)
                        .extracting(TopicTagTotalResponse.TopicTagResponse::content)
                        .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무", "아이스브레이킹");
                    assertThat(tags)
                        .extracting(TopicTagTotalResponse.TopicTagResponse::topicTagId)
                        .doesNotContainNull();
                });
        }

        @Test
        @DisplayName("대화 태그가 존재하지 않으면 빈 리스트를 반환한다.")
        void emptyRequest() {

            // When
            TopicTagTotalResponse result = topicTagService.getAllTopicTags();

            // Then
            assertThat(result.topicTags()).isEmpty();
        }
    }
}
