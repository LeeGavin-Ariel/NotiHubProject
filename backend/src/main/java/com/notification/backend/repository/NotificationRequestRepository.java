package com.notification.backend.repository;

import com.notification.backend.domain.NotificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {
    List<NotificationRequest> findByStatus(String status);
}