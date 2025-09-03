package com.egitron.gestaopedidos.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "error_log")
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 4000)
    private String message;

    @Lob
    @Column(name = "stacktrace")
    private String stacktrace;

    @Column(length = 1000, name = "context")
    private String context;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @PrePersist
    public void prePersist() {
        if (createdAtUtc == null) {
            createdAtUtc = LocalDateTime.now(ZoneOffset.UTC);
        }
    }

    // --- getters/setters ---
    public Integer getId() { return id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStacktrace() { return stacktrace; }
    public void setStacktrace(String stacktrace) { this.stacktrace = stacktrace; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public LocalDateTime getCreatedAtUtc() { return createdAtUtc; }
    public void setCreatedAtUtc(LocalDateTime createdAtUtc) { this.createdAtUtc = createdAtUtc; }
}
