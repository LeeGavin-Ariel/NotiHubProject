package com.notification.backend.kafka;

import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.kafka.sender.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NotificationRouter {

    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationRouter(List<NotificationSender> senders) {
        this.senders = senders.stream()
            .collect(Collectors.toMap(NotificationSender::channel, sender -> sender));
    }

    public void route(NotificationRequest request) {
        NotificationSender sender = senders.get(request.getChannel());

        if (sender == null) {
            log.error("[Router] 지원하지 않는 채널 - {}", request.getChannel());
            return;
        }

        sender.send(request);
    }
}