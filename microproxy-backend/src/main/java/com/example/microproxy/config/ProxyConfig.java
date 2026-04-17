package com.example.microproxy.config;

import com.example.microproxy.proxy.LoggingProxy;
import com.example.microproxy.proxy.MicroserviceProxy;
import com.example.microproxy.repository.AuditLogRepository;
import com.example.microproxy.service.InventoryService;
import com.example.microproxy.service.OrderService;
import com.example.microproxy.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Bean
    public MicroserviceProxy<Object> inventoryProxy(
            InventoryService inventoryService,
            AuditLogRepository auditLogRepository,
            ObjectMapper objectMapper
    ) {
        return new LoggingProxy<>(inventoryService, auditLogRepository, objectMapper);
    }

    @Bean
    public MicroserviceProxy<Object> orderProxy(
            OrderService orderService,
            AuditLogRepository auditLogRepository,
            ObjectMapper objectMapper
    ) {
        return new LoggingProxy<>(orderService, auditLogRepository, objectMapper);
    }

    @Bean
    public MicroserviceProxy<Object> paymentProxy(
            PaymentService paymentService,
            AuditLogRepository auditLogRepository,
            ObjectMapper objectMapper
    ) {
        return new LoggingProxy<>(paymentService, auditLogRepository, objectMapper);
    }
}
