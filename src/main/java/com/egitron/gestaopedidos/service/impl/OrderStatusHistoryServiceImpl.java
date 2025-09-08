// src/main/java/com/egitron/gestaopedidos/service/impl/OrderStatusHistoryServiceImpl.java
package com.egitron.gestaopedidos.service.impl;

import com.egitron.gestaopedidos.model.OrderStatusHistory;
import com.egitron.gestaopedidos.model.Order;
import com.egitron.gestaopedidos.repository.OrderStatusHistoryRepository;
import com.egitron.gestaopedidos.service.OrderStatusHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {

    private final OrderStatusHistoryRepository historyRepository;

    public OrderStatusHistoryServiceImpl(OrderStatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional
    public void recordStatusChange(Order order, String newStatus, String changedBy, String note) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setOrder(order);
        h.setStatus(newStatus);
        h.setChangedBy(changedBy);
        h.setNote(note);
        historyRepository.save(h);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> listByOrder(Integer orderId) {
        return historyRepository.findByOrderOrderIdOrderByChangedAtUtcDesc(orderId);
    }
}
