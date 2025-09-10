package com.egitron.gestaopedidos.api.error;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;


public class ApiError {

    private final OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
    private final int status;   //  400, 404, 500
    private final String error; //  "Bad Request", "Not Found", "Internal Server Error"
    private final String message;
    private final String path;    // endpoint que deu erro (ex.: /api/orders/123)

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}
