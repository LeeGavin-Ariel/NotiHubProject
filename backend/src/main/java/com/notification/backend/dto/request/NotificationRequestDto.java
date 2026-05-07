package com.notification.backend.dto.request;

import com.notification.backend.domain.NotificationChannel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class NotificationRequestDto {

    private String senderApiKey;
    private NotificationChannel channel;
    private String recipient;
    private String templateCode;
    private Map<String, String> variables;
}