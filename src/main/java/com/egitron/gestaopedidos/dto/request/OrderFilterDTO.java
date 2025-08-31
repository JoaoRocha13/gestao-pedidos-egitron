package com.egitron.gestaopedidos.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderFilterDTO {

    private String status;          // PENDING / APPROVED / REJECTED
    private String clientEmail;     // filtro exato por email
    private String search;          // pesquisa livre (nome/email)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdFrom;   // filtrar pedidos criados a partir desta data

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdTo;     // filtrar pedidos até esta data

    private BigDecimal minAmount;   // valor mínimo
    private BigDecimal maxAmount;   // valor máximo

    public OrderFilterDTO() {}

    // getters e setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public OffsetDateTime getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(OffsetDateTime createdFrom) { this.createdFrom = createdFrom; }

    public OffsetDateTime getCreatedTo() { return createdTo; }
    public void setCreatedTo(OffsetDateTime createdTo) { this.createdTo = createdTo; }

    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }

    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
}
