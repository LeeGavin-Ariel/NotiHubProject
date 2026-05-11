package com.notification.backend.kafka.sender;

import com.notification.backend.common.config.KakaoTokenManager;
import com.notification.backend.domain.NotificationChannel;
import com.notification.backend.domain.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoSender implements NotificationSender {

    private final RestTemplate restTemplate;
    private final KakaoTokenManager kakaoTokenManager;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.KAKAO;
    }

    @Override
    public void send(NotificationRequest request) {
        try {
            // 카카오 메시지 API → 본인 카카오톡으로 메시지 발송
            // 액세스 토큰 필요 (KakaoTokenManager가 관리)
            sendMessage(kakaoTokenManager.getAccessToken(), request);
            log.info("[KAKAO] 발송 완료 - 수신자: {}", request.getRecipient());

        } catch (HttpClientErrorException.Unauthorized e) {
            // 401 → 액세스 토큰 만료 → 자동 갱신 후 재시도
            log.warn("[KAKAO] 토큰 만료 감지 → 자동 갱신 시도");
            String newToken = kakaoTokenManager.refreshAccessToken();
            sendMessage(newToken, request);
            log.info("[KAKAO] 토큰 갱신 후 발송 완료 - 수신자: {}", request.getRecipient());

        } catch (Exception e) {
            log.error("[KAKAO] 발송 실패 - 수신자: {}, 오류: {}", request.getRecipient(), e.getMessage());
            throw e;
        }
    }

    private void sendMessage(String token, NotificationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);

        String messageJson = String.format(
                "{\"object_type\":\"text\",\"text\":\"%s\\n%s\",\"link\":{\"web_url\":\"\"}}",
                request.getTitle(),
                request.getContent()
        );

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", messageJson);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        restTemplate.postForObject(
                "https://kapi.kakao.com/v2/api/talk/memo/default/send",
                entity,
                String.class
        );
    }
}