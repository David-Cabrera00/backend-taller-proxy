package com.example.microproxy.service;

import com.example.microproxy.dto.PageResponse;
import com.example.microproxy.dto.ServiceSummaryDto;
import com.example.microproxy.model.AuditLog;
import com.example.microproxy.model.ExecutionStatus;
import com.example.microproxy.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final AuditLogRepository auditLogRepository;

    public MetricsService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<ServiceSummaryDto> getSummary() {
        List<AuditLog> logs = auditLogRepository.findAll();

        Map<String, List<AuditLog>> groupedLogs = logs.stream()
                .collect(Collectors.groupingBy(AuditLog::serviceId));

        return groupedLogs.entrySet().stream()
                .map(entry -> {
                    String serviceId = entry.getKey();
                    List<AuditLog> serviceLogs = entry.getValue();

                    long totalCalls = serviceLogs.size();
                    long successCalls = serviceLogs.stream()
                            .filter(log -> log.status() == ExecutionStatus.SUCCESS)
                            .count();
                    long errorCalls = serviceLogs.stream()
                            .filter(log -> log.status() == ExecutionStatus.ERROR)
                            .count();

                    double successRate = totalCalls == 0 ? 0 : (successCalls * 100.0) / totalCalls;
                    double errorRate = totalCalls == 0 ? 0 : (errorCalls * 100.0) / totalCalls;
                    double averageResponseTimeMs = serviceLogs.stream()
                            .mapToLong(AuditLog::durationMs)
                            .average()
                            .orElse(0.0);

                    return new ServiceSummaryDto(
                            serviceId,
                            totalCalls,
                            successCalls,
                            errorCalls,
                            round(successRate),
                            round(errorRate),
                            round(averageResponseTimeMs)
                    );
                })
                .sorted(Comparator.comparing(ServiceSummaryDto::serviceId))
                .toList();
    }

    public PageResponse<AuditLog> getLogs(
            String service,
            String status,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {
        List<AuditLog> filteredLogs = auditLogRepository.findAll().stream()
                .filter(log -> service == null || service.isBlank() || log.serviceId().equalsIgnoreCase(service))
                .filter(log -> status == null || status.isBlank() || log.status().name().equalsIgnoreCase(status))
                .filter(log -> from == null || !log.timestamp().isBefore(from))
                .filter(log -> to == null || !log.timestamp().isAfter(to))
                .sorted(Comparator.comparing(AuditLog::timestamp).reversed())
                .toList();

        int totalElements = filteredLogs.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<AuditLog> pageContent = filteredLogs.subList(fromIndex, toIndex);
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                pageContent,
                page,
                size,
                totalElements,
                totalPages,
                page >= totalPages - 1
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
