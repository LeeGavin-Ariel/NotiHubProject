package com.notification.backend.kafka.sender;

import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackSender implements NotificationSender {

    private final RestTemplate restTemplate;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SLACK;
    }

    @Override
    public void send(NotificationRequest request) {
        try {

            String botToken = request.getSender().getSlackBotToken();
            String userId = request.getRecipient();

            // 1. DM 채널 열기
            // conversations.open API → 유저 ID로 DM 채널 ID 발급
            String channelId = openDmChannel(botToken, userId);

            // 2. 메시지 발송
            // chat.postMessage API → 채널 ID로 메시지 전송
            sendMessage(botToken, channelId, request.getTitle(), request.getContent());

            log.info("[SLACK] 발송 완료 - 수신자: {}", userId);

        } catch (Exception e) {
            log.error("[SLACK] 발송 실패 - 수신자: {}, 오류: {}", request.getRecipient(), e.getMessage());
            throw e;
        }
    }

    private String openDmChannel(String botToken, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(botToken);

        Map<String, String> body = new HashMap<>();
        body.put("users", userId);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        // RestTemplate → HTTP 요청을 보내는 객체
        // Slack API에 HTTP POST 요청으로 메시지 전송
        Map response = restTemplate.postForObject(
                "https://slack.com/api/conversations.open",
                entity,
                Map.class
        );

        Map channel = (Map) ((Map) response).get("channel");
        return (String) channel.get("id");
    }

    private void sendMessage(String botToken, String channelId, String title, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(botToken);

        Map<String, String> body = new HashMap<>();
        body.put("channel", channelId);
        body.put("text", "*" + title + "*\n" + content);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        restTemplate.postForObject(
                "https://slack.com/api/chat.postMessage",
                entity,
                Map.class
        );
    }
}