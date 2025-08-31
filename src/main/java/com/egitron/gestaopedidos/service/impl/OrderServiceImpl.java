package com.egitron.gestaopedidos.service.impl;

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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    @Override
    public OrderDTO create(CreateOrderDTO dto) {
        // TODO: validação externa do cliente (fase 2)
        Client client = findOrCreateClient(dto.getClientName(), dto.getClientEmail());

        Order order = new Order();
        order.setClient(client);
        order.setTotalAmount(dto.getAmount());
        order.setCurrentStatus(normalizeStatusOrDefault(dto.getStatus(), "PENDING"));

        order = orderRepository.save(order);
        // TODO: guardar histórico de estados
        return toDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO update(Integer orderId, UpdateOrderDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        boolean changed = false;

        // Cliente
        if (dto.getClientEmail() != null || dto.getClientName() != null) {
            String newName = dto.getClientName() != null ? dto.getClientName() : order.getClient().getName();
            String newEmail = dto.getClientEmail() != null ? dto.getClientEmail() : order.getClient().getEmail();
            Client client = findOrCreateClient(newName, newEmail);

            if (!order.getClient().getEmail().equalsIgnoreCase(newEmail)) {
                order.setClient(client);
                changed = true;
            } else if (!Objects.equals(order.getClient().getName(), newName)) {
                order.getClient().setName(newName);
                changed = true;
            }
        }

        // Valor
        if (dto.getAmount() != null) {
            validatePositive(dto.getAmount());
            if (!dto.getAmount().equals(order.getTotalAmount())) {
                order.setTotalAmount(dto.getAmount());
                changed = true;
            }
        }

        // Estado
        if (dto.getStatus() != null) {
            String newStatus = normalizeStatus(dto.getStatus());
            if (!Objects.equals(newStatus, order.getCurrentStatus())) {
                order.setCurrentStatus(newStatus);
                changed = true;
                // TODO: registar histórico
            }
        }

        if (changed) {
            order = orderRepository.save(order);
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

    // ---------- Specifications (Java 8 safe) ----------

    private Specification<Order> buildSpec(final OrderFilterDTO f) {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                Join<Order, Client> clientJoin = root.join("client", JoinType.LEFT);

                // status
                if (hasText(f.getStatus())) {
                    String st = normalizeStatus(f.getStatus());
                    predicates.add(cb.equal(cb.upper(root.<String>get("currentStatus")), st));
                }

                // datas
                if (f.getCreatedFrom() != null) {
                    LocalDateTime from = f.getCreatedFrom().toLocalDateTime();
                    predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("createdAtUtc"), from));
                }
                if (f.getCreatedTo() != null) {
                    LocalDateTime to = f.getCreatedTo().toLocalDateTime();
                    predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get("createdAtUtc"), to));
                }

                // valores
                if (f.getMinAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalAmount"), f.getMinAmount()));
                }
                if (f.getMaxAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalAmount"), f.getMaxAmount()));
                }

                // email exato (ignore case)
                if (hasText(f.getClientEmail())) {
                    predicates.add(cb.equal(
                            cb.lower(clientJoin.<String>get("email")),
                            f.getClientEmail().trim().toLowerCase()
                    ));
                }

                // pesquisa livre (nome/email)
                if (hasText(f.getSearch())) {
                    String like = "%" + f.getSearch().trim().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(clientJoin.<String>get("name")), like),
                            cb.like(cb.lower(clientJoin.<String>get("email")), like)
                    ));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    private Client findOrCreateClient(String name, String email) {
        if (!hasText(email)) {
            throw new BadRequestException("Client email is required.");
        }
        email = email.trim();
        name = (name == null) ? null : name.trim();

        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client != null) {
            boolean changed = false;
            if (name != null && !name.equals(client.getName())) {
                client.setName(name);
                changed = true;
            }
            if (changed) {
                client = clientRepository.save(client);
            }
            return client;
        }

        Client created = new Client();
        created.setName(name);
        created.setEmail(email);
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
