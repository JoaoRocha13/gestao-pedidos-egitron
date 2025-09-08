
package com.egitron.gestaopedidos.service;

import com.egitron.gestaopedidos.model.OrderStatusHistory;
import com.egitron.gestaopedidos.model.Order;

import java.util.List;

public interface OrderStatusHistoryService {
    void recordStatusChange(Order order, String newStatus, String changedBy, String note);
    List<OrderStatusHistory> listByOrder(Integer orderId);
}
