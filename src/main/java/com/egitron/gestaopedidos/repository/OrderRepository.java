package com.egitron.gestaopedidos.repository;

import com.egitron.gestaopedidos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
