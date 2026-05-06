package com.notification.backend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

// Spring Security에서 인증 정보를 담는 객체
public class ApiKeyAuthToken extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuthToken(String apiKey) {
        //Sender sender = senderRepository.findByApiKey(apiKey);
        //new SimpleGrantedAuthority(sender.getRole());
        super(List.of(new SimpleGrantedAuthority("ROLE_API")));
        this.apiKey = apiKey;
        setAuthenticated(true);
    }

    @Override
    // 비밀번호 같은 credential 반환
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    // 인증된 주체 반환
    public Object getPrincipal() {
        return apiKey;
    }
}