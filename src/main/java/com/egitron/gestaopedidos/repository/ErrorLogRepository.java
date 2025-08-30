package com.egitron.gestaopedidos.repository;

import com.egitron.gestaopedidos.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
