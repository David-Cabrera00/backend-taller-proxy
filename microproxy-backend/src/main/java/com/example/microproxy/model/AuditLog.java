package com.example.microproxy.model;

import java.time.LocalDateTime;

public record AuditLog(
        String requestId,
        String serviceId,
        String operation,
        long durationMs,
        ExecutionStatus status,
        LocalDateTime timestamp,
        String inputParams,
        String responseBody,
        String errorMessage
) {
}
