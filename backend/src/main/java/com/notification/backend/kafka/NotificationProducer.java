package com.notification.backend.kafka;

import com.notification.backend.domain.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    // notification.request 토픽 → 알림 요청 메시지 카테고리
    private static final String TOPIC = "notification.request";

    private final KafkaTemplate<String, Long> kafkaTemplate;

    public void send(NotificationRequest request) {
        // 토픽, 키, 값 순서 (키 - 같은 키는 같은 파티션으로 가도록 함)
        // notification.request 토픽에 넣는다.
        kafkaTemplate.send(TOPIC, request.getId().toString(), request.getId());
        log.info("[Producer] 메시지 전송 - requestId: {}", request.getId());
    }
}