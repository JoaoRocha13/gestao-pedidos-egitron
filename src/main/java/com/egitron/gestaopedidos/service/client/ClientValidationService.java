package com.egitron.gestaopedidos.service.client;

public interface ClientValidationService {
    ValidationResult validate(String name, String email);

    final class ValidationResult {
        private final boolean valid;
        private final String reason;
        private final String externalId;

        public ValidationResult(boolean valid, String reason, String externalId) {
            this.valid = valid;
            this.reason = reason;
            this.externalId = externalId;
        }
        public boolean isValid() { return valid; }
        public String getReason() { return reason; }
        public String getExternalId() { return externalId; }
    }
}
