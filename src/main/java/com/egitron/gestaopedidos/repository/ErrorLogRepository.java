package com.egitron.gestaopedidos.repository;

import com.egitron.gestaopedidos.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    List<ErrorLog> findByOccurredAtUtcBetweenOrderByOccurredAtUtcAsc(
            LocalDateTime fromUtc, LocalDateTime toUtc
    );
}
