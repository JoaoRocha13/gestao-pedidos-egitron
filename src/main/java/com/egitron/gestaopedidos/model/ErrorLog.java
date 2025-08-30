package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ErrorLog")
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorId;

    private LocalDateTime occurredAtUtc;

    @Column(nullable = false, length = 20)
    private String level;

    @Column(length = 120)
    private String source;

    @Column(length = 255)
    private String endpoint;

    @Column(nullable = false, length = 400)
    private String message;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String details;

    // getters & setters
    public Long getErrorId() { return errorId; }
    public void setErrorId(Long errorId) { this.errorId = errorId; }
    public LocalDateTime getOccurredAtUtc() { return occurredAtUtc; }
    public void setOccurredAtUtc(LocalDateTime occurredAtUtc) { this.occurredAtUtc = occurredAtUtc; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
