package com.egitron.gestaopedidos.service;

public interface ErrorLogService {
    void log(String message, Throwable t, String context);
}
