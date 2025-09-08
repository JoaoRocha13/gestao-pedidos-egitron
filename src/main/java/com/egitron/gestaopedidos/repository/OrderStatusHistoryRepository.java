// src/main/java/com/egitron/gestaopedidos/repository/OrderStatusHistoryRepository.java
package com.egitron.gestaopedidos.repository;

import com.egitron.gestaopedidos.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findByOrderOrderIdOrderByChangedAtUtcDesc(Integer orderId);
}
