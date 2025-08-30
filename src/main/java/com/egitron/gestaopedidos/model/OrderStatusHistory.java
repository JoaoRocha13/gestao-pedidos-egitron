package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "OrderStatusHistory")
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 400)
    private String note;

    private LocalDateTime changedAtUtc;
    private String changedBy;

    // getters & setters
    public Integer getHistoryId() { return historyId; }
    public void setHistoryId(Integer historyId) { this.historyId = historyId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getChangedAtUtc() { return changedAtUtc; }
    public void setChangedAtUtc(LocalDateTime changedAtUtc) { this.changedAtUtc = changedAtUtc; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}
