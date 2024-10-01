package org.example.tokpik_be.notification.dto.response;

import java.time.LocalTime;

public record NotificationScheduledResponse(
    String receiverToken,
    String talkTopicTitle,
    String talkTopicSubtitle,
    LocalTime startTime,
    LocalTime endTime,
    int intervalMinutes
) {

}
