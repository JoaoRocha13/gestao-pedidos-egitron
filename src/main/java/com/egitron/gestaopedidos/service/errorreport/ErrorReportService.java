package com.egitron.gestaopedidos.service.errorreport;

import java.time.LocalDateTime;

public interface ErrorReportService {
    String buildPlainText(LocalDateTime fromUtc, LocalDateTime toUtc, int tzOffsetMinutes);
}
