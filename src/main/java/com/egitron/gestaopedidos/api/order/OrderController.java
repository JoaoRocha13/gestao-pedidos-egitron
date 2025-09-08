package com.egitron.gestaopedidos.api.order;

import com.egitron.gestaopedidos.dto.request.CreateOrderDTO;
import com.egitron.gestaopedidos.dto.request.UpdateOrderDTO;
import com.egitron.gestaopedidos.dto.request.OrderFilterDTO;
import com.egitron.gestaopedidos.dto.response.OrderDTO;
import com.egitron.gestaopedidos.dto.response.OrderStatusHistoryDTO;
import com.egitron.gestaopedidos.model.OrderStatusHistory;
import com.egitron.gestaopedidos.service.OrderService;
import com.egitron.gestaopedidos.service.OrderStatusHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusHistoryService orderStatusHistoryService; // NEW

    public OrderController(OrderService orderService,
                           OrderStatusHistoryService orderStatusHistoryService) { // NEW
        this.orderService = orderService;
        this.orderStatusHistoryService = orderStatusHistoryService; // NEW
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody CreateOrderDTO body) {
        OrderDTO created = orderService.create(body);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> search(@ModelAttribute OrderFilterDTO filter, Pageable pageable) {
        return ResponseEntity.ok(orderService.search(filter, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Integer id, @Valid @RequestBody UpdateOrderDTO body) {
        return ResponseEntity.ok(orderService.update(id, body));
    }

    // NEW: GET /api/orders/{orderId}/history
    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderStatusHistoryDTO>> getOrderHistory(@PathVariable Integer orderId) {
        // ensure order exists (throws 404 if not)
        orderService.findById(orderId);

        List<OrderStatusHistoryDTO> result = orderStatusHistoryService
                .listByOrder(orderId)
                .stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Mapper for history entity -> DTO
    private OrderStatusHistoryDTO toHistoryDTO(OrderStatusHistory h) {
        OrderStatusHistoryDTO dto = new OrderStatusHistoryDTO();
        dto.setHistoryId(h.getHistoryId());
        dto.setStatus(h.getStatus());
        dto.setChangedAtUtc(h.getChangedAtUtc());
        dto.setChangedBy(h.getChangedBy());
        dto.setNote(h.getNote());
        return dto;
    }

    @GetMapping("/boom")
    public void boom() {
        throw new RuntimeException("boom test");
    }
}
