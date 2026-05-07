package com.notification.backend.kafka;

import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.repository.NotificationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationRouter notificationRouter;

    // notification.request 토픽을 구독하는 리스너
    @KafkaListener(topics = "notification.request", groupId = "notification-group")
    public void consume(Long requestId) {
        log.info("[Consumer] 메시지 수신 - requestId: {}", requestId);

        NotificationRequest request = notificationRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 - id: " + requestId));

        notificationRouter.route(request);
    }
}