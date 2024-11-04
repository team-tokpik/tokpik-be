package org.example.tokpik_be.notification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import org.example.tokpik_be.notification.dto.request.NotificationCreateRequest;
import org.example.tokpik_be.notification.dto.response.NotificationDetailResponse;
import org.example.tokpik_be.notification.dto.response.NotificationDetailResponse.NotificationTalkTopicResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse.NotificationTalkTopicTypeResponse;
import org.example.tokpik_be.notification.service.NotificationCommandService;
import org.example.tokpik_be.notification.service.NotificationQueryService;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class NotificationControllerTest extends ControllerTestSupport {

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private NotificationQueryService notificationQueryService;

    @InjectMocks
    private NotificationController notificationController;

    @Override
    protected Object initController() {

        return notificationController;
    }

    @DisplayName("사용자는 알림을 삭제할 수 있다.")
    @Test
    void deleteNotification() throws Exception {
        // given
        long notificationId = 1L;

        doNothing().when(notificationCommandService)
            .deleteNotification(anyLong(), eq(notificationId));

        // when
        ResultActions resultActions = mockMvc.perform(
            delete("/users/notifications/{notificationId}", notificationId));

        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("사용자는 알림 목록을 조회할 수 있다.")
    @Test
    void getNotifications() throws Exception {
        // given
        NotificationTalkTopicTypeResponse notificationTalkTopicType =
            new NotificationTalkTopicTypeResponse(1L, "친목");

        List<NotificationTalkTopicTypeResponse> notificationTalkTopicTypes = List.of(
            notificationTalkTopicType,
            notificationTalkTopicType,
            notificationTalkTopicType);

        LocalDateTime now = LocalDateTime.now();
        LocalTime notificationStartTime = now.toLocalTime();
        LocalTime notificationEndTime = notificationStartTime.plusHours(1);
        LocalDate noticeDate = now.toLocalDate();
        int notificationInterval = 15;
        String notificationName = "알림 이름";
        long notificationTotal = 4;

        List<NotificationResponse> notificationResponses = LongStream.range(1, 4)
            .mapToObj(notificationId -> new NotificationResponse(notificationId,
                noticeDate,
                notificationStartTime,
                notificationEndTime,
                notificationInterval,
                notificationName,
                notificationTotal,
                notificationTalkTopicTypes))
            .toList();

        NotificationsResponse response = new NotificationsResponse(notificationResponses,
            5L, true, false);

        given(notificationQueryService.getNotifications(anyLong(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/users/notifications"));

        // then
        NotificationResponse notificationResponse = notificationResponses.get(0);
        NotificationTalkTopicTypeResponse notificationTalkTopicTypeResponse =
            notificationTalkTopicTypes.get(0);

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.nextCursorId").value(response.nextCursorId()))
            .andExpect(jsonPath("$.first").value(response.first()))
            .andExpect(jsonPath("$.last").value(response.last()))
            .andExpect(jsonPath("$.contents.length()")
                .value(notificationResponses.size()))
            .andExpect(jsonPath("$.contents[0].notificationId")
                .value(notificationResponse.notificationId()))
            .andExpect(jsonPath("$.contents[0].noticeDate")
                .value(notificationResponse.noticeDate().toString()))
            .andExpect(jsonPath("$.contents[0].notificationStartTime")
                .value(notificationResponse.notificationStartTime().format(localTimeFormatter)))
            .andExpect(jsonPath("$.contents[0].notificationEndTime")
                .value(notificationResponse.notificationEndTime().format(localTimeFormatter)))
            .andExpect(jsonPath("$.contents[0].notificationInterval")
                .value(notificationResponse.notificationInterval()))
            .andExpect(jsonPath("$.contents[0].notificationName")
                .value(notificationResponse.notificationName()))
            .andExpect(jsonPath("$.contents[0].notificationTopicTotal")
                .value(notificationResponse.notificationTopicTotal()))
            .andExpect(jsonPath("$.contents[0].notificationTalkTopicTypes.length()")
                .value(notificationTalkTopicTypes.size()))
            .andExpect(jsonPath("$.contents[0].notificationTalkTopicTypes[0].topicTypeId")
                .value(notificationTalkTopicTypeResponse.topicTypeId()))
            .andExpect(
                jsonPath("$.contents[0].notificationTalkTopicTypes[0].topicTypeContent")
                    .value(notificationTalkTopicTypeResponse.topicTypeContent()));
    }

    @DisplayName("사용자는 알림 상세 내역을 조회할 수 있다.")
    @Test
    void getNotificationDetail() throws Exception {
        // given
        String talkTopicTitle = "대화 주제 제목";
        String talkTopicContent = "대화 주제 종류 내용";
        LocalDateTime now = LocalDateTime.now();
        LocalTime notificationStartTime = now.toLocalTime();
        LocalTime notificationEndTime = notificationStartTime.plusHours(1);
        int notificationIntervalMinutes = 10;

        List<NotificationTalkTopicResponse> notificationTalkTopics = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int minutes = notificationIntervalMinutes * i;
            LocalTime noticeTime = notificationStartTime.plusMinutes(minutes);
            long talkTopicTypeId = i + 1;

            NotificationTalkTopicResponse notificationTalkTopic = new NotificationTalkTopicResponse(
                talkTopicTitle,
                talkTopicTypeId,
                talkTopicContent,
                noticeTime);
            notificationTalkTopics.add(notificationTalkTopic);
        }

        NotificationDetailResponse response = new NotificationDetailResponse("알림 이름",
            notificationStartTime,
            notificationEndTime,
            notificationIntervalMinutes,
            notificationTalkTopics);

        given(notificationQueryService.getNotificationDetail(anyLong(), anyLong()))
            .willReturn(response);

        long notificationId = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/users/notifications/{notificationId}/details", notificationId));

        // then
        NotificationTalkTopicResponse notificationTalkTopicResponse = notificationTalkTopics.get(0);

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.notificationName").value(response.notificationName()))
            .andExpect(jsonPath("$.notificationStartTime")
                .value(response.notificationStartTime().format(localTimeFormatter)))
            .andExpect(jsonPath("$.notificationEndTime")
                .value(response.notificationEndTime().format(localTimeFormatter)))
            .andExpect(jsonPath("$.notificationIntervalMinutes")
                .value(response.notificationIntervalMinutes()))
            .andExpect(jsonPath("$.notificationTalkTopics.length()")
                .value(notificationTalkTopics.size()))
            .andExpect(jsonPath("$.notificationTalkTopics[0].talkTopicTitle")
                .value(notificationTalkTopicResponse.talkTopicTitle()))
            .andExpect(jsonPath("$.notificationTalkTopics[0].talkTopicTypeId")
                .value(notificationTalkTopicResponse.talkTopicTypeId()))
            .andExpect(jsonPath("$.notificationTalkTopics[0].talkTopicContent")
                .value(notificationTalkTopicResponse.talkTopicContent()))
            .andExpect(jsonPath("$.notificationTalkTopics[0].noticeTime")
                .value(notificationTalkTopicResponse.noticeTime().format(localTimeFormatter)));
    }

    @Nested
    @DisplayName("알림 생성 시 ")
    class CreateNotificationTest {

        private final String notificationName = "알림 이름";
        private final long scrapId = 1L;
        private final List<Long> notificationTalkTopicIds = List.of(2L, 1L, 3L);
        private final LocalDateTime now = LocalDateTime.now();
        private final LocalTime notificationStartTime = now.toLocalTime();
        private final LocalTime notificationEndTime = notificationStartTime.plusHours(1);
        private final LocalDate noticeDate = now.toLocalDate();
        private final int notificationIntervalMinutes = 10;

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                notificationTalkTopicIds,
                noticeDate,
                notificationStartTime,
                notificationEndTime,
                notificationIntervalMinutes);

            doNothing().when(notificationCommandService)
                .createNotification(anyLong(), any(NotificationCreateRequest.class));

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @DisplayName("알림 이름은 필수값이며 유효한 문자열만 허용한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void invalidNotificationName(String notificationName) throws Exception {
            // given
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                notificationTalkTopicIds,
                noticeDate,
                notificationStartTime,
                notificationEndTime,
                notificationIntervalMinutes);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.notificationName")
                    .value("알림 이름은 필수값"));
        }

        @DisplayName("알림 지정 대화 주제 ID는 필수값이다.")
        @Test
        void withoutNotificationTalkTopicIds() throws Exception {
            // given
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                null,
                noticeDate,
                notificationStartTime,
                notificationEndTime,
                notificationIntervalMinutes);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.notificationTalkTopicIds")
                    .value("알림 지정 대화 주제 ID들은 필수값"));
        }

        @DisplayName("알림 지정 대화 주제 ID들에 null이 포함될 수 없다.")
        @Test
        void notificationTalkTopicIdsContainNull() throws Exception {
            // given
            List<Long> notificationTalkTopicIds = Arrays.asList(2L, 1L, 3L, null);
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                notificationTalkTopicIds,
                noticeDate,
                notificationStartTime,
                notificationEndTime,
                notificationIntervalMinutes);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['notificationTalkTopicIds[3]']")
                    .value("대화 주제 ID는 not null"));
        }

        @DisplayName("알림 지정 날짜는 필수값이다.")
        @Test
        void withoutNoticeDate() throws Exception {
            // given
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                notificationTalkTopicIds,
                null,
                notificationStartTime,
                notificationEndTime,
                notificationIntervalMinutes);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.noticeDate")
                    .value("알림 지정 날짜는 필수값"));
        }

        @DisplayName("알림 시작 시간은 필수값이다.")
        @Test
        void withoutNotificationStartTime() throws Exception {
            // given
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                notificationTalkTopicIds,
                noticeDate,
                null,
                notificationEndTime,
                notificationIntervalMinutes);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.notificationStartTime")
                    .value("알림 시작 시간은 필수값"));
        }

        @DisplayName("알림 종료 시간은 필수값이다.")
        @Test
        void withoutNotificationEndTime() throws Exception {
            // given
            NotificationCreateRequest request = new NotificationCreateRequest(notificationName,
                scrapId,
                notificationTalkTopicIds,
                noticeDate,
                notificationStartTime,
                null,
                notificationIntervalMinutes);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.notificationEndTime")
                    .value("알림 종료 시간은 필수값"));
        }
    }
}
