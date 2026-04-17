package com.example.microproxy.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService implements BusinessService<Object> {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    public InventoryService() {
        stock.put("P-100", 50);
        stock.put("P-200", 30);
        stock.put("P-300", 80);
    }

    @Override
    public String getServiceId() {
        return "inventory";
    }

    @Override
    public Object execute(String operation, Object... params) {
        Map<String, Object> payload = extractPayload(params);

        return switch (operation) {
            case "checkStock" -> checkStock(payload);
            case "addStock" -> addStock(payload);
            case "removeStock" -> removeStock(payload);
            default -> throw new IllegalArgumentException("Invalid inventory operation: " + operation);
        };
    }

    private Map<String, Object> checkStock(Map<String, Object> payload) {
        String productId = getString(payload, "productId");
        int currentStock = stock.getOrDefault(productId, 0);

        return Map.of(
                "service", "InventoryService",
                "operation", "checkStock",
                "productId", productId,
                "stock", currentStock
        );
    }

    private Map<String, Object> addStock(Map<String, Object> payload) {
        String productId = getString(payload, "productId");
        int quantity = getInt(payload, "quantity");

        stock.put(productId, stock.getOrDefault(productId, 0) + quantity);

        return Map.of(
                "service", "InventoryService",
                "operation", "addStock",
                "productId", productId,
                "newStock", stock.get(productId)
        );
    }

    private Map<String, Object> removeStock(Map<String, Object> payload) {
        String productId = getString(payload, "productId");
        int quantity = getInt(payload, "quantity");
        int currentStock = stock.getOrDefault(productId, 0);

        if (quantity > currentStock) {
            throw new RuntimeException("Not enough stock for product " + productId);
        }

        stock.put(productId, currentStock - quantity);

        return Map.of(
                "service", "InventoryService",
                "operation", "removeStock",
                "productId", productId,
                "newStock", stock.get(productId)
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

    private int getInt(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing parameter: " + key);
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
