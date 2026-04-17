package com.example.microproxy.service;

import org.springframework.stereotype.Service;
import com.example.microproxy.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService implements BusinessService<Object> {

    private final Map<String, Map<String, Object>> payments = new ConcurrentHashMap<>();

    @Override
    public String getServiceId() {
        return "payments";
    }

    @Override
    public Object execute(String operation, Object... params) {
        simulateRandomFailure();

        Map<String, Object> payload = extractPayload(params);

        return switch (operation) {
            case "charge" -> charge(payload);
            case "refund" -> refund(payload);
            case "status" -> status(payload);
            default -> throw new IllegalArgumentException("Invalid payment operation: " + operation);
        };
    }

    private Map<String, Object> charge(Map<String, Object> payload) {
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
        double amount = getDouble(payload, "amount");

        Map<String, Object> payment = new ConcurrentHashMap<>();
        payment.put("paymentId", paymentId);
        payment.put("amount", amount);
        payment.put("status", "PAID");

        payments.put(paymentId, payment);

        return Map.of(
                "service", "PaymentService",
                "operation", "charge",
                "payment", payment
        );
    }

    private Map<String, Object> refund(Map<String, Object> payload) {
        String paymentId = getString(payload, "paymentId");
        Map<String, Object> payment = payments.get(paymentId);

        if (payment == null) {
            throw new RuntimeException("Payment not found: " + paymentId);
        }

        payment.put("status", "REFUNDED");

        return Map.of(
                "service", "PaymentService",
                "operation", "refund",
                "payment", payment
        );
    }

    private Map<String, Object> status(Map<String, Object> payload) {
        String paymentId = getString(payload, "paymentId");
        Map<String, Object> payment = payments.get(paymentId);

        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found: " + paymentId);
        }

        return Map.of(
                "service", "PaymentService",
                "operation", "status",
                "payment", payment
        );
    }

    private void simulateRandomFailure() {
        int random = ThreadLocalRandom.current().nextInt(100);
        if (random < 10) {
            throw new RuntimeException("Intentional random failure in PaymentService");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractPayload(Object... params) {
        if (params == null || params.length == 0 || params[0] == null) {
            return Map.of();
        }
        return (Map<String, Object>) params[0];
    }

    private String getString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing parameter: " + key);
        }
        return String.valueOf(value);
    }

    private double getDouble(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing parameter: " + key);
        }
        return Double.parseDouble(String.valueOf(value));
    }
}
