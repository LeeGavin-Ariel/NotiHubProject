package com.notification.backend.kafka.sender;

import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SlackSender implements NotificationSender {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SLACK;
    }

    @Override
    public void send(NotificationRequest request) {
        // 3주차에 Slack Webhook 실제 연동 예정
        log.info("[SLACK] 발송 - 수신자: {}, 제목: {}", request.getRecipient(), request.getTitle());
    }
}