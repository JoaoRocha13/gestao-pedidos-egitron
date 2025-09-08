package com.egitron.gestaopedidos.service.errorreport;

import com.egitron.gestaopedidos.service.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class ErrorReportOrchestrator {
    private final ErrorReportService reportService;
    private final EmailService emailService;

    @Value("${error.report.enabled:true}")
    private boolean enabled;

    @Value("${error.report.to}")
    private String to;

    @Value("${error.report.lookbackHours:1}")
    private int lookbackHours;

    @Value("${error.report.maxItems:200}")
    private int maxItems;

    public ErrorReportOrchestrator(ErrorReportService reportService, EmailService emailService) {
        this.reportService = reportService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${error.report.cron}")
    public void sendDaily() {
        if (!enabled) return;

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime from = now.minusHours(lookbackHours);

        String body = reportService.buildPlainText(from, now, 0);

        emailService.sendText(to, "[GestaoPedidos] Relatório de Erros", body);
    }
}