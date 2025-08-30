package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", nullable = false)
    private Client client;

    @Column(nullable = false, length = 40, unique = true)
    private String referenceCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false, length = 20)
    private String currentStatus;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderStatusHistory> statusHistory;

    // getters & setters
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAtUtc() { return createdAtUtc; }
    public void setCreatedAtUtc(LocalDateTime createdAtUtc) { this.createdAtUtc = createdAtUtc; }
    public LocalDateTime getUpdatedAtUtc() { return updatedAtUtc; }
    public void setUpdatedAtUtc(LocalDateTime updatedAtUtc) { this.updatedAtUtc = updatedAtUtc; }
    public List<OrderStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<OrderStatusHistory> statusHistory) { this.statusHistory = statusHistory; }
}
