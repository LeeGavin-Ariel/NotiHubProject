package com.notification.backend.controller;

import com.notification.backend.domain.NotificationFailureLog;
import com.notification.backend.domain.NotificationRequest;
import com.notification.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 전체 발송 내역 조회
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationRequest>> getAllRequests() {
        return ResponseEntity.ok(adminService.getAllRequests());
    }

    // 상태별 발송 내역 조회
    @GetMapping("/notifications/status/{status}")
    public ResponseEntity<List<NotificationRequest>> getRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminService.getRequestsByStatus(status));
    }

    // 전체 실패 로그 조회
    @GetMapping("/failures")
    public ResponseEntity<List<NotificationFailureLog>> getFailureLogs() {
        return ResponseEntity.ok(adminService.getFailureLogs());
    }

    // 특정 요청의 실패 로그 조회
    @GetMapping("/failures/{requestId}")
    public ResponseEntity<List<NotificationFailureLog>> getFailureLogsByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(adminService.getFailureLogsByRequestId(requestId));
    }
}