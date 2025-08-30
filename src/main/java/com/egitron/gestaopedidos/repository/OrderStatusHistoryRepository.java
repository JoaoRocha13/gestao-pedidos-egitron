package com.egitron.gestaopedidos.repository;

import com.egitron.gestaopedidos.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {
}
