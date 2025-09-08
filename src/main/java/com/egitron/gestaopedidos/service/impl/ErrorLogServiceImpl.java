package com.egitron.gestaopedidos.service.impl;

import com.egitron.gestaopedidos.model.ErrorLog;
import com.egitron.gestaopedidos.repository.ErrorLogRepository;
import com.egitron.gestaopedidos.service.ErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogRepository repo;

    public ErrorLogServiceImpl(ErrorLogRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public void log(Exception ex, HttpServletRequest req) {
        ErrorLog l = new ErrorLog();
        l.setLevel("ERROR"); // obrigatório na BD
        l.setSource(ex != null ? ex.getClass().getName() : "unknown");
        if (req != null) {
            l.setEndpoint(req.getMethod() + " " + req.getRequestURI());
        }
        l.setMessage(safeMessage(ex));
        l.setDetails(stacktraceOf(ex));
        repo.save(l);
    }

    private String safeMessage(Throwable t) {
        if (t == null) return "Unknown error";
        return t.getMessage() != null ? t.getMessage() : t.toString();
    }

    private String stacktraceOf(Throwable t) {
        if (t == null) return "";
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
