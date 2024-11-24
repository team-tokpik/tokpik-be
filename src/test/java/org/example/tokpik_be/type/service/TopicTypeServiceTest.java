package org.example.tokpik_be.type.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.domain.UserTopicType;
import org.example.tokpik_be.type.dto.request.UserTopicTypesRequest;
import org.example.tokpik_be.type.dto.response.TopicTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserTopicTypeResponse;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.type.repository.UserTopicTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TopicTypeServiceTest {

    @Mock
    private TopicTypeRepository topicTypeRepository;

    @Mock
    private UserTopicTypeRepository userTopicTypeRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private TopicTypeService topicTypeService;

    private User mockUser(long userId) {
        return new User("ex@example.com", "https://www.example.com/profile-photo") {
            @Override
            public Long getId() {
                return userId;
            }
        };
    }

    @DisplayName("사용자의 대화 타입을 조회할 수 있다.")
    @Test
    void getMyTopicTypes() {
        // given
        long userId = 1L;
        User user = mockUser(userId);
        given(userQueryService.findById(userId)).willReturn(user);

        List<TopicType> topicTypes = List.of(
            new TopicType("사랑과 연애"),
            new TopicType("비즈니스와 업무"));

        List<UserTopicType> userTopicTypes = List.of(
            new UserTopicType(userId, topicTypes.get(0)),
            new UserTopicType(userId, topicTypes.get(1)));

        given(userTopicTypeRepository.findByUserId(anyLong())).willReturn(userTopicTypes);

        // when
        UserTopicTypeResponse response = topicTypeService.getUserTopicTypes(userId);

        // then
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.talkTopicTypes())
            .hasSize(2)
            .map(UserTopicTypeResponse.TopicTypeDTO::content)
            .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무");
    }

    @Nested
    @DisplayName("사용자 대화 타입 수정 시 ")
    class UpdateTopicTypesTest {
        @DisplayName("성공한다.")
        @Test
        void updateMyTopicTypes() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            given(userQueryService.findById(userId)).willReturn(user);

            List<TopicType> topicTypes = List.of(
                new TopicType(1L, "사랑과 연애"),
                new TopicType(3L, "아이스브레이킹"));

            given(topicTypeRepository.findAllById(List.of(1L, 3L))).willReturn(topicTypes);

            List<Long> newTypes = List.of(1L, 3L);
            UserTopicTypesRequest request = new UserTopicTypesRequest(newTypes);

            List<UserTopicType> userTopicTypes = List.of(
                new UserTopicType(userId, topicTypes.get(0)),
                new UserTopicType(userId, topicTypes.get(1)));

            given(userTopicTypeRepository.findByUserId(userId)).willReturn(userTopicTypes);

            // when
            UserTopicTypeResponse response = topicTypeService.updateUserTopicTypes(userId, request);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.talkTopicTypes())
                .hasSize(2)
                .allSatisfy(types -> {
                    assertThat(types.content()).isIn("사랑과 연애", "아이스브레이킹");
                });
        }

        @DisplayName("중복된 값을 요청 데이터에 포함하면 예외가 발생한다.")
        @Test
        void duplicateTypes() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            given(userQueryService.findById(userId)).willReturn(user);

            List<Long> duplicateType = List.of(1L, 1L);
            UserTopicTypesRequest request = new UserTopicTypesRequest(duplicateType);

            // when & then
            assertThatThrownBy(() -> topicTypeService.updateUserTopicTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TypeException.DUPLICATE_TYPES);
        }

        @DisplayName("존재하지 않는 값이면 예외가 발생한다.")
        @Test
        void typeNotFound() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            given(userQueryService.findById(userId)).willReturn(user);

            long invalidTypeId = 10L;

            List<Long> invalidType = List.of(invalidTypeId);

            given(topicTypeRepository.findAllById(invalidType)).willReturn(List.of());
            UserTopicTypesRequest request = new UserTopicTypesRequest(invalidType);

            // when & then
            assertThatThrownBy(() -> topicTypeService.updateUserTopicTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TypeException.TYPE_NOT_FOUND);
        }
    }

    @DisplayName("모든 대화 타입을 조회할 수 있다.")
    @Test
    void getAllTopicTypes() {
        // given
        List<TopicType> topicTypes = List.of(
            new TopicType(1L,"사랑과 연애"),
            new TopicType(2L, "비즈니스와 업무"),
            new TopicType(3L, "아이스브레이킹"));

        given(topicTypeRepository.findAll()).willReturn(topicTypes);

        // when
        TopicTypeTotalResponse result = topicTypeService.getAllTopicTypes();

        // then
        assertThat(result.topicTypes())
            .hasSize(3)
            .extracting(TopicTypeTotalResponse.TopicTypeResponse::content)
            .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무", "아이스브레이킹");
    }
}
