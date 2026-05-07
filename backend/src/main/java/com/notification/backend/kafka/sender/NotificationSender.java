package com.notification.backend.kafka.sender;

import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;

public interface NotificationSender {
    
    // 각 채널 구현체가 반환할 채널 타입
    NotificationChannel channel();
    
    // 실제 발송 메서드
    void send(NotificationRequest request);
}