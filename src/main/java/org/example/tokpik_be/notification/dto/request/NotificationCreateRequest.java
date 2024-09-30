package org.example.tokpik_be.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record NotificationCreateRequest(
    @Schema(type = "number", description = "스크랩 ID", example = "1")
    long scrapId,

    @Schema(type = "array", description = "알림 지정한 대화 주제 ID들, 선택 순서대로 정렬 필요",
        example = "[2,1,3]")
    @NotEmpty(message = "알림 지정 대화 주제 ID들은 필수값")
    List<@NotNull(message = "대화 주제 ID는 not null") Long> notificationTalkTopicIds,

    @Schema(type = "string", description = "알림 지정 날짜, yyyy-MM-dd 포맷", example = "1998-04-08")
    @NotNull(message = "알림 지정 날짜는 필수값")
    LocalDate noticeDate,

    @Schema(type = "string", description = "알림 시작 시간, 00:00~23:59", example = "09:00")
    @NotNull(message = "알림 시작 시간은 필수값")
    LocalTime notificationStartTime,

    @Schema(type = "string", description = "알림 종료 시간, 00:00~23:59", example = "11:00")
    @NotNull(message = "알림 종료 시간은 필수값")
    LocalTime notificationEndTime,

    @Schema(type = "number", description = "알림 간격(분 단위)", example = "10")
    int notificationIntervalMinutes
) {

}
