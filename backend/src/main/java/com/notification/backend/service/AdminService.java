package com.notification.backend.service;

import com.notification.backend.domain.NotificationFailureLog;
import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.repository.NotificationFailureLogRepository;
import com.notification.backend.repository.NotificationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationFailureLogRepository failureLogRepository;

    // 전체 발송 내역 조회
    @Transactional(readOnly = true)
    public List<NotificationRequest> getAllRequests() {
        return notificationRequestRepository.findAll();
    }

    // 상태별 발송 내역 조회 (PENDING / SENT / FAILED)
    @Transactional(readOnly = true)
    public List<NotificationRequest> getRequestsByStatus(String status) {
        return notificationRequestRepository.findByStatus(status);
    }

    // 실패 로그 조회
    @Transactional(readOnly = true)
    public List<NotificationFailureLog> getFailureLogs() {
        return failureLogRepository.findAll();
    }

    // 특정 요청의 실패 로그 조회
    @Transactional(readOnly = true)
    public List<NotificationFailureLog> getFailureLogsByRequestId(Long requestId) {
        return failureLogRepository.findByRequestId(requestId);
    }
}