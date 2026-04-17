package com.example.microproxy.controller;

import com.example.microproxy.dto.ApiResponse;
import com.example.microproxy.proxy.MicroserviceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final MicroserviceProxy<Object> inventoryProxy;
    private final MicroserviceProxy<Object> orderProxy;
    private final MicroserviceProxy<Object> paymentProxy;

    public ServiceController(
            @Qualifier("inventoryProxy") MicroserviceProxy<Object> inventoryProxy,
            @Qualifier("orderProxy") MicroserviceProxy<Object> orderProxy,
            @Qualifier("paymentProxy") MicroserviceProxy<Object> paymentProxy
    ) {
        this.inventoryProxy = inventoryProxy;
        this.orderProxy = orderProxy;
        this.paymentProxy = paymentProxy;
    }

    @PostMapping("/inventory/{operation}")
    public ResponseEntity<ApiResponse> executeInventoryOperation(
            @PathVariable String operation,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        Object result = inventoryProxy.execute(operation, payload == null ? Map.of() : payload);

        return ResponseEntity.ok(
                new ApiResponse(true, "Inventory operation executed successfully", result)
        );
    }

    @PostMapping("/orders/{operation}")
    public ResponseEntity<ApiResponse> executeOrderOperation(
            @PathVariable String operation,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        Object result = orderProxy.execute(operation, payload == null ? Map.of() : payload);

        return ResponseEntity.ok(
                new ApiResponse(true, "Order operation executed successfully", result)
        );
    }

    @PostMapping("/payments/{operation}")
    public ResponseEntity<ApiResponse> executePaymentOperation(
            @PathVariable String operation,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        Object result = paymentProxy.execute(operation, payload == null ? Map.of() : payload);

        return ResponseEntity.ok(
                new ApiResponse(true, "Payment operation executed successfully", result)
        );
    }
}
