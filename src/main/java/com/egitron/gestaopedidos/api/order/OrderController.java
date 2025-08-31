package com.egitron.gestaopedidos.api.order;

import com.egitron.gestaopedidos.dto.request.CreateOrderDTO;
import com.egitron.gestaopedidos.dto.request.UpdateOrderDTO;
import com.egitron.gestaopedidos.dto.request.OrderFilterDTO;
import com.egitron.gestaopedidos.dto.response.OrderDTO;
import com.egitron.gestaopedidos.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {   // <- renomeado

    private final OrderService orderService;

    public OrderController(OrderService orderService) { // <- renomeado
        this.orderService = orderService;
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
}
