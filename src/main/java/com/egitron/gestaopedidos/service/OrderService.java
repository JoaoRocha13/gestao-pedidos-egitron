package com.egitron.gestaopedidos.service;

import com.egitron.gestaopedidos.dto.request.CreateOrderDTO;
import com.egitron.gestaopedidos.dto.request.UpdateOrderDTO;
import com.egitron.gestaopedidos.dto.request.OrderFilterDTO;
import com.egitron.gestaopedidos.dto.response.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDTO create(CreateOrderDTO dto);

    OrderDTO update(Integer orderId, UpdateOrderDTO dto);

    OrderDTO findById(Integer orderId);

    Page<OrderDTO> search(OrderFilterDTO filter, Pageable pageable);
}
