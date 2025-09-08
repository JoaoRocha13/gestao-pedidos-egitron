package com.egitron.gestaopedidos.api.mock;

import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/mock-api/clients")
public class ClientValidationMockController {

    @PostMapping("/verify")
    public ValidationResponse verify(@RequestBody ValidationRequest req) {
        boolean emailFormatOk = req.getEmail() != null && req.getEmail().matches(".+@.+\\..+");
        boolean domainAllowed = req.getEmail() != null && !req.getEmail().toLowerCase().endsWith("@example.com");
        boolean nameOk = req.getName() != null && req.getName().trim().length() >= 2;

        boolean valid = emailFormatOk && domainAllowed && nameOk;
        String reason;
        if (!emailFormatOk)      reason = "E-mail inválido";
        else if (!domainAllowed) reason = "Domínio bloqueado";
        else if (!nameOk)        reason = "Nome inválido";
        else                     reason = "OK";

        ValidationResponse resp = new ValidationResponse();
        resp.setValid(valid);
        resp.setReason(reason);
        resp.setExternalId(valid ? UUID.randomUUID().toString() : null);
        return resp;
    }

    public static class ValidationRequest {
        private String name;
        private String email;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    public static class ValidationResponse {
        private boolean valid;
        private String reason;
        private String externalId;
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getExternalId() { return externalId; }
        public void setExternalId(String externalId) { this.externalId = externalId; }
    }
}
