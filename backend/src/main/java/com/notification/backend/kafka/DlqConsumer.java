package com.notification.backend.kafka;

import com.notification.backend.domain.NotificationFailureLog;
import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.repository.NotificationFailureLogRepository;
import com.notification.backend.repository.NotificationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqConsumer {

    private static final int MAX_RETRY_COUNT = 3;

    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationFailureLogRepository failureLogRepository;
    private final NotificationRouter notificationRouter;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    // DLQ 토픽을 구독하는 리스너
    // dlqKafkaListenerContainerFactory를 사용해서 별도 consumer group으로 처리
    @Transactional
    @KafkaListener(
            topics = "notification.dlq",
            groupId = "notification-dlq-group",
            containerFactory = "dlqKafkaListenerContainerFactory"
    )
    public void consume(Long requestId) {
        log.info("[DLQ Consumer] 메시지 수신 - requestId: {}", requestId);

        NotificationRequest request = notificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 - id: " + requestId));

        if (request.getRetryCount() < MAX_RETRY_COUNT) {
            try {
                request.incrementRetryCount();
                log.info("[DLQ Consumer] 재시도 - requestId: {}, retryCount: {}", requestId, request.getRetryCount());
                notificationRouter.route(request);

            } catch (Exception e) {
                log.error("[DLQ Consumer] 재시도 실패 - requestId: {}, retryCount: {}", requestId, request.getRetryCount());

                // 실패 로그 DB 저장
                saveFailureLog(request, e.getMessage());

                // 재시도 실패 시 다시 DLQ로 produce
                kafkaTemplate.send("notification.dlq", requestId.toString(), requestId);
            }
        } else {
            // 최대 재시도 횟수 초과 시 FAILED 상태로 변경
            log.error("[DLQ Consumer] 최대 재시도 초과 → FAILED - requestId: {}", requestId);
            request.markAsFailed();

            // 최종 실패 로그 DB 저장
            saveFailureLog(request, "최대 재시도 횟수 초과");
        }
    }

    private void saveFailureLog(NotificationRequest request, String errorMessage) {
        // 실패 로그 Entity 생성 후 DB 저장
        NotificationFailureLog failureLog = NotificationFailureLog.builder()
                .request(request)
                .errorMessage(errorMessage)
                .retryCount(request.getRetryCount())
                .build();

        failureLogRepository.save(failureLog);
        log.error("[DLQ Consumer] 실패 로그 저장 완료 - requestId: {}, error: {}", request.getId(), errorMessage);
    }
}