package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;


@Entity
@Table(name = "[Order]") //
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId") // PK
    private Integer orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", nullable = false)  // FK explícita
    private Client client;

    @Column(name = "currentStatus", nullable = false, length = 20)
    private String currentStatus;

    @Column(name = "totalAmount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "createdAtUtc")
    private LocalDateTime createdAtUtc;

    @Column(name = "updatedAtUtc")
    private LocalDateTime updatedAtUtc;


    @Column(name = "validated")
    private Boolean validated;

    @Column(name = "validationReason", length = 255)
    private String validationReason;

    @Column(name = "validationExternalId", length = 64)
    private String validationExternalId;

    @Column(name = "validatedAt")
    private LocalDateTime validatedAt;



    /*CALLBACKS JPA*/
    @PrePersist
    protected void onCreate() {
        this.createdAtUtc = LocalDateTime.now();
        this.updatedAtUtc = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAtUtc = LocalDateTime.now();
    }


    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAtUtc() { return createdAtUtc; }
    public void setCreatedAtUtc(LocalDateTime createdAtUtc) { this.createdAtUtc = createdAtUtc; }

    public LocalDateTime getUpdatedAtUtc() { return updatedAtUtc; }
    public void setUpdatedAtUtc(LocalDateTime updatedAtUtc) { this.updatedAtUtc = updatedAtUtc; }

    public Boolean getValidated() {
        return validated;
    }
    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getValidationReason() {
        return validationReason;
    }
    public void setValidationReason(String validationReason) {
        this.validationReason = validationReason;
    }

    public String getValidationExternalId() {
        return validationExternalId;
    }
    public void setValidationExternalId(String validationExternalId) {
        this.validationExternalId = validationExternalId;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }
    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }


}
