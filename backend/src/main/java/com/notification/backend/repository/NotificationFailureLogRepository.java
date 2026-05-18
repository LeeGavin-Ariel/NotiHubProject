package com.notification.backend.repository;

import com.notification.backend.domain.NotificationFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationFailureLogRepository extends JpaRepository<NotificationFailureLog, Long> {
    List<NotificationFailureLog> findByRequestId(Long requestId);
}