package com.notification.backend.kafka;

import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.domain.NotificationSuccessLog;
import com.notification.backend.repository.NotificationRequestRepository;
import com.notification.backend.repository.NotificationSuccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private static final String DLQ_TOPIC = "notification.dlq";

    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationSuccessLogRepository successLogRepository;
    private final NotificationRouter notificationRouter;

    // 발송 실패 시 DLQ 토픽으로 메시지를 보내기 위한 KafkaTemplate
    private final KafkaTemplate<String, Long> kafkaTemplate;

    // notification.request 토픽을 구독하는 리스너
    @Transactional
    @KafkaListener(topics = "notification.request", groupId = "notification-group")
    public void consume(Long requestId) {
        log.info("[Consumer] 메시지 수신 - requestId: {}", requestId);

        NotificationRequest request = notificationRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 - id: " + requestId));

        try {
            notificationRouter.route(request);

            // 발송 성공 시 status SENT로 변경
            request.markAsSent();

            // 성공 로그 DB 저장
            NotificationSuccessLog successLog = NotificationSuccessLog.builder()
                    .request(request)
                    .channel(request.getChannel())
                    .recipient(request.getRecipient())
                    .build();

            successLogRepository.save(successLog);
            log.info("[Consumer] 발송 성공 - requestId: {}", requestId);
        } catch (Exception e) {
// 발송 실패 시 DLQ 토픽으로 requestId 전송
            // DlqConsumer가 이를 받아서 재시도 처리
            log.error("[Consumer] 발송 실패 → DLQ로 전송 - requestId: {}", requestId);
            kafkaTemplate.send(DLQ_TOPIC, requestId.toString(), requestId);
        }
    }
}