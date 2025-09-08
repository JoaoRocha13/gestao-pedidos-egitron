package com.egitron.gestaopedidos.service.errorreport.impl;

import com.egitron.gestaopedidos.model.ErrorLog;
import com.egitron.gestaopedidos.repository.ErrorLogRepository;
import com.egitron.gestaopedidos.service.errorreport.ErrorReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ErrorReportServiceImpl implements ErrorReportService {

    private final ErrorLogRepository repo;

    public ErrorReportServiceImpl(ErrorLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public String buildPlainText(LocalDateTime fromUtc, LocalDateTime toUtc, int tzOffsetMinutes) {
        List<ErrorLog> logs =
                repo.findByOccurredAtUtcBetweenOrderByOccurredAtUtcAsc(fromUtc, toUtc);

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(tzOffsetMinutes * 60);

        StringBuilder sb = new StringBuilder();
        sb.append("Relatório de Erros ")
                .append(fromUtc).append(" -> ").append(toUtc).append("\n")
                .append("Total: ").append(logs.size()).append("\n");

        for (ErrorLog l : logs) {
            sb.append("\n")
                    .append(l.getOccurredAtUtc()
                            .atOffset(ZoneOffset.UTC)
                            .withOffsetSameInstant(offset)
                            .toLocalDateTime())
                    .append(" [").append(nullToEmpty(l.getLevel())).append("] ")
                    .append(nullToEmpty(l.getSource())).append(" @ ")
                    .append(nullToEmpty(l.getEndpoint())).append("\n")
                    .append(nullToEmpty(l.getMessage())).append("\n");
        }
        return sb.toString();
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }
}
