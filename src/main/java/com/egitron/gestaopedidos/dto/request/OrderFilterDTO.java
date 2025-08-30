package com.egitron.gestaopedidos.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderFilterDTO {

    private String status;             // PENDING/APPROVED/REJECTED
    private String clientEmail;        // equals
    private String search;             // free text: name/email (handled in service)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdTo;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private Integer page;              // optional pagination hint
    private Integer size;              // optional pagination hint
    private String sort;               // e.g. "createdAt,desc"

    public OrderFilterDTO() {}

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

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
}
