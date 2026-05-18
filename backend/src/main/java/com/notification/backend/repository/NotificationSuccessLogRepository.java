package com.notification.backend.repository;

import com.notification.backend.domain.NotificationSuccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSuccessLogRepository extends JpaRepository<NotificationSuccessLog, Long> {
}