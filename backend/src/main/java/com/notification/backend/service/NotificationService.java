package com.notification.backend.service;

import com.notification.backend.dto.request.NotificationRequestDto;
import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.domain.NotificationTemplate;
import com.notification.backend.domain.Sender;
import com.notification.backend.kafka.NotificationProducer;
import com.notification.backend.repository.NotificationRequestRepository;
import com.notification.backend.repository.NotificationTemplateRepository;
import com.notification.backend.repository.SenderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SenderRepository senderRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void send(NotificationRequestDto dto) {

        // 1. Sender 조회
        String apiKey = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Sender sender = senderRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 API Key"));

        // 2. 템플릿 조회
        NotificationTemplate template = templateRepository.findByTemplateCode(dto.getTemplateCode())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 템플릿"));

        // 3. 템플릿 변수 치환
        String title = replaceVariables(template.getTitleTemplate(), dto.getVariables());
        String content = replaceVariables(template.getContentTemplate(), dto.getVariables());

        // 4. NotificationRequest 저장
        NotificationRequest request = NotificationRequest.builder()
            .sender(sender)
            .template(template)
            .channel(dto.getChannel())
            .recipient(dto.getRecipient())
            .variables(dto.getVariables().toString())
            .title(title)
            .content(content)
            .build();

        notificationRequestRepository.save(request);

        // 5. Kafka produce
        notificationProducer.send(request);
    }

    private String replaceVariables(String template, java.util.Map<String, String> variables) {
        if (variables == null) return template;
        String result = template;
        for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}