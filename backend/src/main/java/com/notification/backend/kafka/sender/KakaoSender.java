package com.notification.backend.kafka.sender;

import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaoSender implements NotificationSender {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.KAKAO;
    }

    @Override
    public void send(NotificationRequest request) {
        // Mock 구현
        log.info("[KAKAO] 발송 - 수신자: {}, 제목: {}", request.getRecipient(), request.getTitle());
    }
}