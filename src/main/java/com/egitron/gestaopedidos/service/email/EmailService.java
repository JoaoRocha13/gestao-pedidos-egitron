package com.egitron.gestaopedidos.service.email;

public interface EmailService {
    void sendText(String to, String subject, String text);
}
