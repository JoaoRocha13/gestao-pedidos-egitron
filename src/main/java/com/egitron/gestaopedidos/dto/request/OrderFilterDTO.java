package com.egitron.gestaopedidos.dto.request;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;


public class OrderFilterDTO {
    private String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdTo;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(OffsetDateTime createdFrom) { this.createdFrom = createdFrom; }

    public OffsetDateTime getCreatedTo() { return createdTo; }
    public void setCreatedTo(OffsetDateTime createdTo) { this.createdTo = createdTo; }
}
