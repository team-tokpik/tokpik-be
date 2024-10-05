package org.example.tokpik_be.tag.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.tag.dto.request.UserPlaceTagsRequest;
import org.example.tokpik_be.tag.dto.response.PlaceTagTotalResponse;
import org.example.tokpik_be.tag.dto.response.UserPlaceTagResponse;
import org.example.tokpik_be.tag.service.PlaceTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaceTagControllerTest extends ControllerTestSupport {
    @Mock
    private PlaceTagService placeTagService;
    @InjectMocks
    private PlaceTagController placeTagController;
    @Override
    protected Object initController(){
        return placeTagController;
    }
    private final long userId = 1L;
    @Nested
    @DisplayName("사용자 장소 태그 조회 시 ")
    class GetUserPlaceTagsTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // Given
            UserPlaceTagResponse response = new UserPlaceTagResponse(userId, List.of(
                new UserPlaceTagResponse.PlaceTagDTO(1L, "Tag 1")
            ));
            given(placeTagService.getUserPlaceTags(userId)).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/place-tags").requestAttr("userId", userId));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.placeTopicTags[0].id").value(1L))
                .andExpect(jsonPath("$.placeTopicTags[0].content").value("Tag 1"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(placeTagService.getUserPlaceTags(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/place-tags").requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("사용자 장소 태그 수정 시 ")
    class UpdateUserPlaceTagsTest {
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // Given
            UserPlaceTagsRequest request = new UserPlaceTagsRequest(List.of(1L, 2L));
            UserPlaceTagResponse response = new UserPlaceTagResponse(userId, List.of(
                new UserPlaceTagResponse.PlaceTagDTO(1L, "Tag 1"),
                new UserPlaceTagResponse.PlaceTagDTO(2L, "Tag 2")
            ));
            given(placeTagService.updateUserPlaceTags(eq(userId), any(UserPlaceTagsRequest.class))).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/place-tags")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.placeTopicTags[0].id").value(1L))
                .andExpect(jsonPath("$.placeTopicTags[0].content").value("Tag 1"))
                .andExpect(jsonPath("$.placeTopicTags[1].id").value(2L))
                .andExpect(jsonPath("$.placeTopicTags[1].content").value("Tag 2"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(placeTagService.getUserPlaceTags(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/place-tags")
                .requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("요청 데이터가 없으면 예외가 발생한다.")
        void noRequestData() throws Exception {
            // Given
            given(placeTagService.updateUserPlaceTags(anyLong(), any(UserPlaceTagsRequest.class)))
                .willThrow(new GeneralException(TagException.INVALID_REQUEST));

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/place-tags")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(TagException.INVALID_REQUEST.getMessage()));
        }
    }
    @Nested
    @DisplayName("장소 태그 전체 조회 시 ")
    class getAllTagsTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception{
            // Given
            PlaceTagTotalResponse response = new PlaceTagTotalResponse(List.of(
                new PlaceTagTotalResponse.PlaceTagResponse(1L, "Tag 1"),
                new PlaceTagTotalResponse.PlaceTagResponse(2L, "Tag 2")
            ));
            given(placeTagService.getAllPlaceTags()).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/place-tags"));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placeTags[0].content").value("Tag 1"))
                .andExpect(jsonPath("$.placeTags[1].content").value("Tag 2"));
        }
    }
}
