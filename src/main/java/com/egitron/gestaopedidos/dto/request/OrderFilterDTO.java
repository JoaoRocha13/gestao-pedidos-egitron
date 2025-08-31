package com.egitron.gestaopedidos.dto.request;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;

/**
 * DTO de filtros de pesquisa para Orders.
 * Requisito: apenas filtrar por estado ou intervalo de datas (criação).
 */
public class OrderFilterDTO {

    // Estado do pedido: PENDING, APPROVED, REJECTED
    private String status;

    // Data inicial (inclusive)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdFrom;

    // Data final (inclusive)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdTo;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(OffsetDateTime createdFrom) { this.createdFrom = createdFrom; }

    public OffsetDateTime getCreatedTo() { return createdTo; }
    public void setCreatedTo(OffsetDateTime createdTo) { this.createdTo = createdTo; }
}
