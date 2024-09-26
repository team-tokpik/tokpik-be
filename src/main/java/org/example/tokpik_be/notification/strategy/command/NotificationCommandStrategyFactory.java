package org.example.tokpik_be.notification.strategy.command;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandStrategyFactory {

    private final Map<Integer, NotificationCommandStrategy> notificationCommandStrategyByIntervalMinutes;

    @Autowired
    public NotificationCommandStrategyFactory(
        List<NotificationCommandStrategy> notificationCommandStrategies) {

        this.notificationCommandStrategyByIntervalMinutes = notificationCommandStrategies.stream()
            .collect(Collectors.toMap(NotificationCommandStrategy::getIntervalMinutes,
                Function.identity()));
    }

    public void deleteByIdAndIntervalMinutes(long notificationId, int intervalMinutes) {
        NotificationCommandStrategy notificationCommandStrategy =
            notificationCommandStrategyByIntervalMinutes.get(intervalMinutes);

        if (Objects.isNull(notificationCommandStrategy)) {
            throw new GeneralException(NotificationException.INVALID_NOTIFICATION_INTERVAL);
        }

        notificationCommandStrategy.deleteById(notificationId);
    }
}
