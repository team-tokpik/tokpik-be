package org.example.tokpik_be.notification.strategy.query;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationQueryStrategyFactory {

    private final Map<Integer, NotificationQueryStrategy> notificationQueryStrategyByInterval;

    @Autowired
    public NotificationQueryStrategyFactory(
        List<NotificationQueryStrategy> notificationQueryStrategies) {

        this.notificationQueryStrategyByInterval = notificationQueryStrategies.stream()
            .collect(Collectors.toMap(
                NotificationQueryStrategy::getIntervalMinutes,
                Function.identity()
            ));
    }

    public BaseNotification findByIdAndIntervalMinutes(long notificationId, int intervalMinutes) {

        NotificationQueryStrategy notificationQueryStrategy = notificationQueryStrategyByInterval
            .get(intervalMinutes);

        if (Objects.isNull(notificationQueryStrategy)) {
            throw new GeneralException(NotificationException.INVALID_NOTIFICATION_INTERVAL);
        }

        return notificationQueryStrategy.findById(notificationId);
    }
}
