package com.notification.backend.repository;

import com.notification.backend.domain.Sender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderRepository extends JpaRepository<Sender, Long> {
    boolean existsByApiKey(String apiKey);
}