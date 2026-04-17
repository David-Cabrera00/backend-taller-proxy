package com.example.microproxy.service;

import com.example.microproxy.proxy.MicroserviceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LoadSimulationService {

    private final MicroserviceProxy<Object> inventoryProxy;
    private final MicroserviceProxy<Object> orderProxy;
    private final MicroserviceProxy<Object> paymentProxy;

    public LoadSimulationService(
            @Qualifier("inventoryProxy") MicroserviceProxy<Object> inventoryProxy,
            @Qualifier("orderProxy") MicroserviceProxy<Object> orderProxy,
            @Qualifier("paymentProxy") MicroserviceProxy<Object> paymentProxy
    ) {
        this.inventoryProxy = inventoryProxy;
        this.orderProxy = orderProxy;
        this.paymentProxy = paymentProxy;
    }

    public Map<String, Object> simulate(int totalCalls) {
        int successCalls = 0;
        int errorCalls = 0;

        for (int i = 0; i < totalCalls; i++) {
            int selectedService = ThreadLocalRandom.current().nextInt(3);

            try {
                switch (selectedService) {
                    case 0 -> inventoryProxy.execute(
                            "checkStock",
                            Map.of("productId", randomProductId())
                    );
                    case 1 -> orderProxy.execute(
                            "createOrder",
                            Map.of(
                                    "customer", "Cliente-" + (i + 1),
                                    "total", ThreadLocalRandom.current().nextDouble(100, 1000)
                            )
                    );
                    case 2 -> paymentProxy.execute(
                            "charge",
                            Map.of("amount", ThreadLocalRandom.current().nextDouble(50, 1500))
                    );
                }
                successCalls++;
            } catch (Exception ex) {
                errorCalls++;
            }
        }

        return Map.of(
                "generatedCalls", totalCalls,
                "successCalls", successCalls,
                "errorCalls", errorCalls
        );
    }

    private String randomProductId() {
        String[] productIds = {"P-100", "P-200", "P-300"};
        return productIds[ThreadLocalRandom.current().nextInt(productIds.length)];
    }
}
