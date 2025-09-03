package com.egitron.gestaopedidos.service.impl;

import com.egitron.gestaopedidos.model.ErrorLog;
import com.egitron.gestaopedidos.repository.ErrorLogRepository;
import com.egitron.gestaopedidos.service.ErrorLogService;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogRepository repo;

    public ErrorLogServiceImpl(ErrorLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void log(String message, Throwable t, String context) {
        ErrorLog e = new ErrorLog();
        e.setMessage(message != null ? message : (t != null ? t.getMessage() : "Unexpected error"));
        e.setContext(context);

        if (t != null) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            e.setStacktrace(sw.toString());
        }

        repo.save(e);
    }
}
