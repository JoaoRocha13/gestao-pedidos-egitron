package com.egitron.gestaopedidos.service;

import javax.servlet.http.HttpServletRequest;

public interface ErrorLogService {
    void log(Exception ex, HttpServletRequest req);
}
