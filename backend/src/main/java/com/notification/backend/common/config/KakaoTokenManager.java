package com.notification.backend.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class KakaoTokenManager {

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    @Value("${kakao.access-token}")
    private String accessToken;

    @Value("${kakao.refresh-token}")
    private String refreshToken;

    private final RestTemplate restTemplate;

    public KakaoTokenManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String refreshAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", restApiKey);
            body.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            Map response = restTemplate.postForObject(
                    "https://kauth.kakao.com/oauth/token",
                    entity,
                    Map.class
            );

            accessToken = (String) response.get("access_token");
            log.info("[KAKAO] 액세스 토큰 자동 갱신 완료");
            return accessToken;

        } catch (Exception e) {
            log.error("[KAKAO] 액세스 토큰 갱신 실패 - {}", e.getMessage());
            throw e;
        }
    }
}