package org.example.tokpik_be.tag.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.UserPlaceTag;
import org.example.tokpik_be.tag.dto.request.UserPlaceTagsRequest;
import org.example.tokpik_be.tag.dto.response.PlaceTagTotalResponse;
import org.example.tokpik_be.tag.dto.response.UserPlaceTagResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class PlaceTagServiceTest extends ServiceTestSupport {
    private PlaceTagService placeTagService;

    @MockBean
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        placeTagService = new PlaceTagService(userPlaceTagRepository, userQueryService, placeTagRepository);
    }

    @Nested
    @DisplayName("사용자 장소 태그 조회 시 ")
    class GetPlaceTagsTest {

        @Test
        @DisplayName("성공한다.")
        void getMyPlaceTags() {
            // Given
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            PlaceTag placeTag1 = new PlaceTag("집");
            PlaceTag placeTag2 = new PlaceTag("회사");
            placeTagRepository.saveAll(Arrays.asList(placeTag1, placeTag2));

            UserPlaceTag userPlaceTag1 = new UserPlaceTag(userId, placeTag1);
            UserPlaceTag userPlaceTag2 = new UserPlaceTag(userId, placeTag2);
            userPlaceTagRepository.saveAll(Arrays.asList(userPlaceTag1, userPlaceTag2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            // When
            UserPlaceTagResponse response = placeTagService.getUserPlaceTags(userId);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.placeTopicTags())
                .hasSize(2)
                .map(UserPlaceTagResponse.PlaceTagDTO::content)
                .containsExactlyInAnyOrder("집", "회사");

            assertThat(response.placeTopicTags())
                .map(UserPlaceTagResponse.PlaceTagDTO::id)
                .doesNotContainNull();

        }
    }

    @Nested
    @DisplayName("사용자 장소 태그 수정 시 ")
    class UpdatePlaceTagsTest {
        @Test
        @DisplayName("성공한다.")
        void updateMyPlaceTags() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            PlaceTag placeTag1 = new PlaceTag("집");
            PlaceTag placeTag2 = new PlaceTag("학교");
            PlaceTag placeTag3 = new PlaceTag("직장");
            placeTagRepository.saveAll(Arrays.asList(placeTag1, placeTag2, placeTag3));

            UserPlaceTag userPlaceTag1 = new UserPlaceTag(userId, placeTag1);
            UserPlaceTag userPlaceTag2 = new UserPlaceTag(userId, placeTag2);
            userPlaceTagRepository.saveAll(Arrays.asList(userPlaceTag1, userPlaceTag2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> newTagIds = Arrays.asList(placeTag1.getId(), placeTag3.getId());
            UserPlaceTagsRequest request = new UserPlaceTagsRequest(newTagIds);

            // When
            UserPlaceTagResponse response = placeTagService.updateUserPlaceTags(userId, request);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.placeTopicTags())
                .hasSize(2)
                .allSatisfy(tags -> {
                    assertThat(tags.content()).isIn("집", "직장");
                    assertThat(tags.id()).isIn(placeTag1.getId(), placeTag3.getId());
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

            PlaceTag placeTag1 = new PlaceTag("집");
            placeTagRepository.save(placeTag1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> duplicateTagIds = Arrays.asList(placeTag1.getId(), placeTag1.getId());
            UserPlaceTagsRequest request = new UserPlaceTagsRequest(duplicateTagIds);

            // Then
            assertThatThrownBy(() -> placeTagService.updateUserPlaceTags(userId, request))
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

            PlaceTag placeTag1 = new PlaceTag("집");
            placeTagRepository.save(placeTag1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);


            List<Long> invalidTagIds = Arrays.asList(placeTag1.getId(), 10L);
            UserPlaceTagsRequest request = new UserPlaceTagsRequest(invalidTagIds);

            // Then
            assertThatThrownBy(() -> placeTagService.updateUserPlaceTags(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TagException.TAG_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("장소 태그 전체 조회 시 ")
    class GetAllPlaceTagsTest {

        @Test
        @DisplayName("성공한다.")
        void getAllPlaceTags() {

            // Given
            PlaceTag placeTag1 = new PlaceTag("집");
            PlaceTag placeTag2 = new PlaceTag("학교");
            PlaceTag placeTag3 = new PlaceTag("직장");
            placeTagRepository.saveAll(Arrays.asList(placeTag1, placeTag2, placeTag3));

            em.flush();
            em.clear();

            // When
            PlaceTagTotalResponse result = placeTagService.getAllPlaceTags();

            // Then
            assertThat(result.placeTags())
                .hasSize(3)
                .satisfies(tags -> {
                    assertThat(tags)
                        .extracting(PlaceTagTotalResponse.PlaceTagResponse::content)
                        .containsExactlyInAnyOrder("집", "학교", "직장");
                    assertThat(tags)
                        .extracting(PlaceTagTotalResponse.PlaceTagResponse::placeTagId)
                        .doesNotContainNull();
                });
        }

        @Test
        @DisplayName("장소 태그가 존재하지 않으면 빈 리스트를 반환한다.")
        void emptyRequest() {
            // When
            PlaceTagTotalResponse result = placeTagService.getAllPlaceTags();

            // Then
            assertThat(result.placeTags()).isEmpty();
        }
    }
}
