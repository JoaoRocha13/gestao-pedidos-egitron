package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "ErrorLog")
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "errorId")
    private Long id;

    @Column(name = "occurredAtUtc", nullable = false)
    private LocalDateTime occurredAtUtc;

    @Column(name = "level", nullable = false, length = 20)
    private String level;                // ex: ERROR, WARN, INFO

    @Column(name = "source", length = 240)
    private String source;

    @Column(name = "endpoint", length = 510)
    private String endpoint;

    @Column(name = "message", length = 800)
    private String message;
    @Lob
    @Column(name = "details")
    private String details;

    @PrePersist
    public void prePersist() {
        if (occurredAtUtc == null) {
            occurredAtUtc = LocalDateTime.now(ZoneOffset.UTC);
        }
        if (level == null) level = "ERROR";
    }


    public Long getId() { return id; }

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
