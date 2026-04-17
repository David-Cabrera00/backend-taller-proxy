package com.example.microproxy.service;

import org.springframework.stereotype.Service;
import com.example.microproxy.exception.ResourceNotFoundException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService implements BusinessService<Object> {

    private final Map<String, Map<String, Object>> orders = new ConcurrentHashMap<>();

    @Override
    public String getServiceId() {
        return "orders";
    }

    @Override
    public Object execute(String operation, Object... params) {
        Map<String, Object> payload = extractPayload(params);

        return switch (operation) {
            case "createOrder" -> createOrder(payload);
            case "getOrder" -> getOrder(payload);
            case "cancelOrder" -> cancelOrder(payload);
            default -> throw new IllegalArgumentException("Invalid order operation: " + operation);
        };
    }

    private Map<String, Object> createOrder(Map<String, Object> payload) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        String customer = getString(payload, "customer");
        double total = getDouble(payload, "total");

        Map<String, Object> order = new ConcurrentHashMap<>();
        order.put("orderId", orderId);
        order.put("customer", customer);
        order.put("total", total);
        order.put("status", "CREATED");

        orders.put(orderId, order);

        return Map.of(
                "service", "OrderService",
                "operation", "createOrder",
                "order", order
        );
    }

    private Map<String, Object> getOrder(Map<String, Object> payload) {
        String orderId = getString(payload, "orderId");
        Map<String, Object> order = orders.get(orderId);

        if (order == null) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }

        return Map.of(
                "service", "OrderService",
                "operation", "getOrder",
                "order", order
        );
    }

    private Map<String, Object> cancelOrder(Map<String, Object> payload) {
        String orderId = getString(payload, "orderId");
        Map<String, Object> order = orders.get(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        order.put("status", "CANCELLED");

        return Map.of(
                "service", "OrderService",
                "operation", "cancelOrder",
                "order", order
        );
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
