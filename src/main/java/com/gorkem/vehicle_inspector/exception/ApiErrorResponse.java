package com.gorkem.vehicle_inspector.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final Map<String, String> validationErrors;

    public ApiErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            Map<String, String> validationErrors
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.validationErrors = validationErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}