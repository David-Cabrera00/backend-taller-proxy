package com.example.microproxy.proxy;

import com.example.microproxy.model.AuditLog;
import com.example.microproxy.model.ExecutionStatus;
import com.example.microproxy.repository.AuditLogRepository;
import com.example.microproxy.service.BusinessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class LoggingProxy<T> implements MicroserviceProxy<T> {

    private final BusinessService<T> target;
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public LoggingProxy(BusinessService<T> target,
                        AuditLogRepository auditLogRepository,
                        ObjectMapper objectMapper) {
        this.target = target;
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public T execute(String operation, Object... params) {
        String requestId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();
        long start = System.currentTimeMillis();

        try {
            T response = target.execute(operation, params);
            long duration = System.currentTimeMillis() - start;

            auditLogRepository.save(new AuditLog(
                    requestId,
                    target.getServiceId(),
                    operation,
                    duration,
                    ExecutionStatus.SUCCESS,
                    timestamp,
                    toJson(params),
                    toJson(response),
                    null
            ));

            return response;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;

            auditLogRepository.save(new AuditLog(
                    requestId,
                    target.getServiceId(),
                    operation,
                    duration,
                    ExecutionStatus.ERROR,
                    timestamp,
                    toJson(params),
                    null,
                    compactError(ex)
            ));

            throw ex;
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }

    private String compactError(Exception ex) {
        String trace = Arrays.stream(ex.getStackTrace())
                .limit(4)
                .map(item -> item.getClassName() + ":" + item.getLineNumber())
                .collect(Collectors.joining(" | "));

        return ex.getClass().getSimpleName() + ": " + ex.getMessage() + " | " + trace;
    }
}
