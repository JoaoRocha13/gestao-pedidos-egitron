// src/main/java/com/egitron/gestaopedidos/model/OrderStatusHistory.java
package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "OrderStatusHistory")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historyId")
    private Long historyId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    @Column(name = "status", nullable = false, length = 30)
    private String status; // snapshot of the status at the time of the change

    @Column(name = "changedAtUtc", nullable = false)
    private LocalDateTime changedAtUtc;

    @Column(name = "changedBy")
    private String changedBy;

    @Column(name = "note")
    private String note;

    @PrePersist
    protected void onCreate() {
        if (this.changedAtUtc == null) {
            this.changedAtUtc = LocalDateTime.now();
        }
    }

    // Getters and setters
    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getChangedAtUtc() { return changedAtUtc; }
    public void setChangedAtUtc(LocalDateTime changedAtUtc) { this.changedAtUtc = changedAtUtc; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
