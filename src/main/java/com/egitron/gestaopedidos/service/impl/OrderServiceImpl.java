package com.egitron.gestaopedidos.service.impl;

import com.egitron.gestaopedidos.service.OrderStatusHistoryService;
import com.egitron.gestaopedidos.service.client.ClientValidationService;
import com.egitron.gestaopedidos.dto.request.CreateOrderDTO;
import com.egitron.gestaopedidos.dto.request.OrderFilterDTO;
import com.egitron.gestaopedidos.dto.request.UpdateOrderDTO;
import com.egitron.gestaopedidos.dto.response.OrderDTO;
import com.egitron.gestaopedidos.exception.BadRequestException;
import com.egitron.gestaopedidos.exception.NotFoundException;
import com.egitron.gestaopedidos.model.Client;
import com.egitron.gestaopedidos.model.Order;
import com.egitron.gestaopedidos.repository.ClientRepository;
import com.egitron.gestaopedidos.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements com.egitron.gestaopedidos.service.OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ClientValidationService clientValidationService;
    private final OrderStatusHistoryService orderStatusHistoryService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository,
                            ClientValidationService clientValidationService,
                            OrderStatusHistoryService orderStatusHistoryService) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.clientValidationService = clientValidationService;
        this.orderStatusHistoryService = orderStatusHistoryService;
    }


    @Override
    @Transactional
    public OrderDTO create(CreateOrderDTO dto) {
        ClientValidationService.ValidationResult v =
                clientValidationService.validate(dto.getClientName(), dto.getClientEmail());

        if (!v.isValid()) {
            throw new BadRequestException("Cliente inválido (validação externa): " + v.getReason());
        }

        Client client = findOrCreateClient(dto.getClientName(), dto.getClientEmail());

        Order order = new Order();
        order.setClient(client);
        order.setTotalAmount(dto.getAmount());
        order.setCurrentStatus(normalizeStatusOrDefault(dto.getStatus(), "PENDING"));

        // persist external validation result on the order
        order.setValidated(Boolean.TRUE);
        order.setValidationReason(v.getReason());
        order.setValidationExternalId(v.getExternalId());
        order.setValidatedAt(java.time.LocalDateTime.now());

        order = orderRepository.save(order);

        // NEW: record initial status in history
        orderStatusHistoryService.recordStatusChange(
                order,
                order.getCurrentStatus(),
                "API",
                "Order created"
        );

        return toDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO update(Integer orderId, UpdateOrderDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        boolean willTouchClient = (hasText(dto.getClientName()) || hasText(dto.getClientEmail()));
        if (willTouchClient) {
            String newName  = hasText(dto.getClientName())  ? dto.getClientName()  : order.getClient().getName();
            String newEmail = hasText(dto.getClientEmail()) ? dto.getClientEmail() : order.getClient().getEmail();

            ClientValidationService.ValidationResult v =
                    clientValidationService.validate(newName, newEmail);
            if (!v.isValid()) {
                throw new BadRequestException("Cliente inválido (validação externa): " + v.getReason());
            }

            // apply changes on client
            if (hasText(dto.getClientName()))  order.getClient().setName(dto.getClientName());
            if (hasText(dto.getClientEmail())) order.getClient().setEmail(dto.getClientEmail());

            // persist external validation result on the order
            order.setValidated(Boolean.TRUE);
            order.setValidationReason(v.getReason());
            order.setValidationExternalId(v.getExternalId());
            order.setValidatedAt(java.time.LocalDateTime.now());
        }

        if (dto.getAmount() != null) {
            if (dto.getAmount().signum() <= 0) throw new BadRequestException("Amount deve ser > 0");
            order.setTotalAmount(dto.getAmount());
        }

        // capture previous status to decide whether to write history
        String previousStatus = order.getCurrentStatus();

        if (hasText(dto.getStatus())) {
            order.setCurrentStatus(normalizeStatus(dto.getStatus()));
        }

        order = orderRepository.save(order);

        // NEW: only record history if status actually changed
        if ((previousStatus == null && order.getCurrentStatus() != null)
                || (previousStatus != null && !previousStatus.equalsIgnoreCase(order.getCurrentStatus()))) {
            orderStatusHistoryService.recordStatusChange(
                    order,
                    order.getCurrentStatus(),
                    "API",
                    "Order status updated"
            );
        }

        return toDTO(order);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDTO findById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        return toDTO(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDTO> search(OrderFilterDTO filter, Pageable pageable) {
        Specification<Order> spec = buildSpec(filter);

        // Ordenação por defeito: createdAtUtc DESC
        Pageable effective = pageable;
        if (effective.getSort().isUnsorted()) {
            effective = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAtUtc")
            );
        }

        return orderRepository.findAll(spec, effective).map(this::toDTO);
    }

    // Specifications (apenas ESTADO e DATAS)

    private Specification<Order> buildSpec(final OrderFilterDTO f) {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                // Filtrar por ESTADO
                if (hasText(f.getStatus())) {
                    String st = normalizeStatus(f.getStatus());
                    predicates.add(cb.equal(cb.upper(root.<String>get("currentStatus")), st));
                }

                // Filtrar por INTERVALO DE DATAS (criação)
                if (f.getCreatedFrom() != null) {
                    LocalDateTime from = f.getCreatedFrom().toLocalDateTime();
                    predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("createdAtUtc"), from));
                }
                if (f.getCreatedTo() != null) {
                    LocalDateTime to = f.getCreatedTo().toLocalDateTime();
                    predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get("createdAtUtc"), to));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    // Helpers

    private Client findOrCreateClient(String name, String email) {
        if (!hasText(email)) {
            throw new BadRequestException("Client email is required.");
        }

        final String emailTrim = email.trim();
        final String nameTrim  = (name == null) ? null : name.trim();

        // tenta obter cliente existente por email
        java.util.Optional<Client> opt = clientRepository.findByEmail(emailTrim);
        if (opt.isPresent()) {
            Client existing = opt.get();
            if (nameTrim != null && !nameTrim.equals(existing.getName())) {
                throw new BadRequestException(
                        "Já existe um cliente com este email (" + emailTrim + ") registado com o nome '" +
                                existing.getName() + "'. Não é permitido criar pedido com nome diferente."
                );
            }
            return existing;
        }

        // cria novo cliente
        Client created = new Client();
        created.setName(nameTrim);
        created.setEmail(emailTrim);
        return clientRepository.save(created);
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");
        }
    }

    private String normalizeStatusOrDefault(String raw, String def) {
        return !hasText(raw) ? def : normalizeStatus(raw);
    }

    private String normalizeStatus(String raw) {
        String s = raw.trim().toUpperCase();
        switch (s) {
            case "PENDING":
            case "APPROVED":
            case "REJECTED":
                return s;
            default:
                throw new BadRequestException("Invalid status: " + raw);
        }
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getOrderId());
        dto.setStatus(order.getCurrentStatus());
        dto.setAmount(order.getTotalAmount());

        if (order.getClient() != null) {
            dto.setClientName(order.getClient().getName());
            dto.setClientEmail(order.getClient().getEmail());
        }

        dto.setCreatedAt(
                order.getCreatedAtUtc() != null
                        ? order.getCreatedAtUtc().atOffset(ZoneOffset.UTC)
                        : null
        );

        return dto;
    }

    /** Java 8 helper (equivalente a String#isBlank em versões novas) */
    private boolean hasText(String s) {
        return s != null && s.trim().length() > 0;
    }
}
