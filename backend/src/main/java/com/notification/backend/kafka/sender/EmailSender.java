package com.notification.backend.kafka.sender;

import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender implements NotificationSender {

    // JavaMailSender → Spring이 제공하는 메일 발송 객체
    // application.yml의 mail 설정을 읽어서 Gmail SMTP 서버에 연결
    private final JavaMailSender mailSender;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipient());
            message.setSubject(request.getTitle());
            message.setText(request.getContent());

            mailSender.send(message);
            log.info("[EMAIL] 발송 완료 - 수신자: {}", request.getRecipient());

        } catch (Exception e) {
            log.error("[EMAIL] 발송 실패 - 수신자: {}, 오류: {}", request.getRecipient(), e.getMessage());
            throw e;
        }
    }
}