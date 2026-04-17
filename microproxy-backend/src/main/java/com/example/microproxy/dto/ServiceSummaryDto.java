package com.example.microproxy.dto;

public record ServiceSummaryDto(
        String serviceId,
        long totalCalls,
        long successCalls,
        long errorCalls,
        double successRate,
        double errorRate,
        double averageResponseTimeMs
) {
}
