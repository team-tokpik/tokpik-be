package org.example.tokpik_be.type.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.domain.UserTopicType;
import org.example.tokpik_be.type.dto.request.UserTopicTypesRequest;
import org.example.tokpik_be.type.dto.response.TopicTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserTopicTypeResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class TopicTypeServiceTest extends ServiceTestSupport {

    private TopicTypeService topicTypeService;

    @MockBean
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        topicTypeService = new TopicTypeService(userTopicTypeRepository, userQueryService, topicTypeRepository);
    }

    @Nested
    @DisplayName("사용자 대화 태그 조회 시 ")
    class GetTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void getMyTopicTypes() {

            // Given
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicType topicType1 = new TopicType("사랑과 연애");
            TopicType topicType2 = new TopicType("비즈니스와 업무");
            topicTypeRepository.saveAll(Arrays.asList(topicType1, topicType2));

            UserTopicType userTopicType1 = new UserTopicType(userId, topicType1);
            UserTopicType userTopicType2 = new UserTopicType(userId, topicType2);
            userTopicTypeRepository.saveAll(Arrays.asList(userTopicType1, userTopicType2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            // When
            UserTopicTypeResponse response = topicTypeService.getUserTopicTypes(userId);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.talkTopicTypes())
                .hasSize(2)
                .map(UserTopicTypeResponse.TopicTypeDTO::content)
                .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무");

            assertThat(response.talkTopicTypes())
                .map(UserTopicTypeResponse.TopicTypeDTO::id)
                .doesNotContainNull();
        }
    }

    @Nested
    @DisplayName("사용자 대화 태그 수정 시 ")
    class UpdateTopicTypesTest {
        @Test
        @DisplayName("성공한다.")
        void updateMyTopicTypes() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicType topicType1 = new TopicType("사랑과 연애");
            TopicType topicType2 = new TopicType("비즈니스와 업무");
            TopicType topicType3 = new TopicType("아이스브레이킹");
            topicTypeRepository.saveAll(Arrays.asList(topicType1, topicType2, topicType3));

            UserTopicType userTopicType1 = new UserTopicType(userId, topicType1);
            UserTopicType userTopicType2 = new UserTopicType(userId, topicType2);
            userTopicTypeRepository.saveAll(Arrays.asList(userTopicType1, userTopicType2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> newTypeIds = Arrays.asList(topicType1.getId(), topicType3.getId());
            UserTopicTypesRequest request = new UserTopicTypesRequest(newTypeIds);

            // When
            UserTopicTypeResponse response = topicTypeService.updateUserTopicTypes(userId, request);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.talkTopicTypes())
                .hasSize(2)
                .allSatisfy(types -> {
                    assertThat(types.content()).isIn("사랑과 연애", "아이스브레이킹");
                    assertThat(types.id()).isIn(topicType1.getId(), topicType3.getId());
                });
        }


        @Test
        @DisplayName("중복된 값을 요청 데이터에 포함하면 예외가 발생한다.")
        void duplicateTypes() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicType topicType1 = new TopicType("사랑과 연애");
            topicTypeRepository.save(topicType1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> duplicateTypeIds = Arrays.asList(topicType1.getId(), topicType1.getId());
            UserTopicTypesRequest request = new UserTopicTypesRequest(duplicateTypeIds);

            // Then
            assertThatThrownBy(() -> topicTypeService.updateUserTopicTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TypeException.DUPLICATE_TYPES.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 값이면 예외가 발생한다.")
        void typeNotFound() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            TopicType topicType1 = new TopicType("사랑과 연애");
            topicTypeRepository.save(topicType1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);


            List<Long> invalidTypeIds = Arrays.asList(topicType1.getId(), 10L);
            UserTopicTypesRequest request = new UserTopicTypesRequest(invalidTypeIds);

            // Then
            assertThatThrownBy(() -> topicTypeService.updateUserTopicTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TypeException.TYPE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("대화 태그 전체 조회 시 ")
    class GetAllTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void getAllTopicTypes() {

            // Given
            TopicType topicType1 = new TopicType("사랑과 연애");
            TopicType topicType2 = new TopicType("비즈니스와 업무");
            TopicType topicType3 = new TopicType("아이스브레이킹");
            topicTypeRepository.saveAll(Arrays.asList(topicType1, topicType2, topicType3));

            em.flush();
            em.clear();

            // When
            TopicTypeTotalResponse result = topicTypeService.getAllTopicTypes();

            // Then
            assertThat(result.topicTypes())
                .hasSize(3)
                .satisfies(types -> {
                    assertThat(types)
                        .extracting(TopicTypeTotalResponse.TopicTypeResponse::content)
                        .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무", "아이스브레이킹");
                    assertThat(types)
                        .extracting(TopicTypeTotalResponse.TopicTypeResponse::topicTypeId)
                        .doesNotContainNull();
                });
        }

        @Test
        @DisplayName("대화 태그가 존재하지 않으면 빈 리스트를 반환한다.")
        void emptyRequest() {

            // When
            TopicTypeTotalResponse result = topicTypeService.getAllTopicTypes();

            // Then
            assertThat(result.topicTypes()).isEmpty();
        }
    }
}
