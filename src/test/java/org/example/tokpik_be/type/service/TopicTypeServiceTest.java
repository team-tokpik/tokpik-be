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

    @Test
    @DisplayName("사용자의 대화 타입을 조회할 수 있다.")
    void getMyTopicTypes() {
        // given
        long userId = 1L;
        User user = mockUser(userId);
        when(userQueryService.findById(userId)).thenReturn(user);

        TopicType topicType1 = new TopicType("사랑과 연애");
        TopicType topicType2 = new TopicType("비즈니스와 업무");

        UserTopicType userTopicType1 = new UserTopicType(userId, topicType1);
        UserTopicType userTopicType2 = new UserTopicType(userId, topicType2);

        List<UserTopicType> userTopicTypes = List.of(userTopicType1, userTopicType2);

        when(userTopicTypeRepository.findByUserId(anyLong())).thenReturn(userTopicTypes);

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
        @Test
        @DisplayName("성공한다.")
        void updateMyTopicTypes() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            when(userQueryService.findById(userId)).thenReturn(user);

            TopicType topicType1 = new TopicType(1L, "사랑과 연애");
            TopicType topicType3 = new TopicType(3L, "아이스브레이킹");

            when(topicTypeRepository.findAllById(List.of(1L, 3L)))
                .thenReturn(List.of(topicType1, topicType3));

            List<Long> newTypes = List.of(1L, 3L);
            UserTopicTypesRequest request = new UserTopicTypesRequest(newTypes);

            UserTopicType userTopicType1 = new UserTopicType(userId, topicType1);
            UserTopicType userTopicType3 = new UserTopicType(userId, topicType3);
            when(userTopicTypeRepository.findByUserId(userId))
                .thenReturn(List.of(userTopicType1, userTopicType3));

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

        @Test
        @DisplayName("중복된 값을 요청 데이터에 포함하면 예외가 발생한다.")
        void duplicateTypes() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> duplicateType = List.of(1L, 1L);
            UserTopicTypesRequest request = new UserTopicTypesRequest(duplicateType);

            // when & then
            assertThatThrownBy(() -> topicTypeService.updateUserTopicTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TypeException.DUPLICATE_TYPES);
        }

        @Test
        @DisplayName("존재하지 않는 값이면 예외가 발생한다.")
        void typeNotFound() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            when(userQueryService.findById(userId)).thenReturn(user);

            long invalidTypeId = 10L;

            List<Long> invalidType = List.of(invalidTypeId);

            when(topicTypeRepository.findAllById(invalidType)).thenReturn(List.of());
            UserTopicTypesRequest request = new UserTopicTypesRequest(invalidType);

            // when & then
            assertThatThrownBy(() -> topicTypeService.updateUserTopicTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TypeException.TYPE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("대화 타입 전체 조회 시 ")
    class GetAllTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void getAllTopicTypes() {
            // given
            TopicType topicType1 = new TopicType(1L,"사랑과 연애");
            TopicType topicType2 = new TopicType(2L, "비즈니스와 업무");
            TopicType topicType3 = new TopicType(3L, "아이스브레이킹");

            when(topicTypeRepository.findAll()).thenReturn(List.of(topicType1, topicType2, topicType3));

            // when
            TopicTypeTotalResponse result = topicTypeService.getAllTopicTypes();

            // then
            assertThat(result.topicTypes())
                .hasSize(3)
                .extracting(TopicTypeTotalResponse.TopicTypeResponse::content)
                .containsExactlyInAnyOrder("사랑과 연애", "비즈니스와 업무", "아이스브레이킹");
        }

        @Test
        @DisplayName("대화 타입이 존재하지 않으면 빈 리스트를 반환한다.")
        void emptyRequest() {
            // given
            when(topicTypeRepository.findAll()).thenReturn(List.of());

            // when
            TopicTypeTotalResponse result = topicTypeService.getAllTopicTypes();

            // then
            assertThat(result.topicTypes()).isEmpty();
        }
    }
}
