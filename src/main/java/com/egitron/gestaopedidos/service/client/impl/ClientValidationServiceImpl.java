package com.egitron.gestaopedidos.service.client.impl;

import com.egitron.gestaopedidos.service.client.ClientValidationService;
import com.egitron.gestaopedidos.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientValidationServiceImpl implements ClientValidationService {

    private final RestTemplate restTemplate;
    private final String url;
    private final boolean enabled;
    private final String failStrategy;

    public ClientValidationServiceImpl(RestTemplate restTemplate,
                                       @Value("${external.client.validation.url}") String url,
                                       @Value("${external.client.validation.enabled:true}") boolean enabled,
                                       @Value("${external.client.validation.failStrategy:FAIL_CLOSED}") String failStrategy) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.enabled = enabled;
        this.failStrategy = failStrategy;
    }

    @Override
    public ValidationResult validate(String name, String email) {
        if (!enabled) return new ValidationResult(true, "validation disabled", null);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<ValidationResponse> resp = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(body, headers), ValidationResponse.class
            );

            ValidationResponse vr = resp.getBody();
            boolean ok = vr != null && vr.isValid();
            String reason = (vr != null ? vr.getReason() : "empty response");
            String extId = (vr != null ? vr.getExternalId() : null);
            return new ValidationResult(ok, reason, extId);

        } catch (Exception ex) {
            if ("FAIL_OPEN".equalsIgnoreCase(failStrategy)) {
                return new ValidationResult(true, "validation skipped (fail open): " + ex.getMessage(), null);
            }
            throw new BadRequestException("Falha na validação externa do cliente: " + ex.getMessage());
        }
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
