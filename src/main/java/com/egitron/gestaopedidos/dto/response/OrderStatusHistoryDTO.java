// src/main/java/com/egitron/gestaopedidos/dto/response/OrderStatusHistoryDTO.java
package com.egitron.gestaopedidos.dto.response;

import java.time.LocalDateTime;

public class OrderStatusHistoryDTO {
    private Long historyId;
    private String status;
    private LocalDateTime changedAtUtc;
    private String changedBy;
    private String note;

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getChangedAtUtc() { return changedAtUtc; }
    public void setChangedAtUtc(LocalDateTime changedAtUtc) { this.changedAtUtc = changedAtUtc; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
