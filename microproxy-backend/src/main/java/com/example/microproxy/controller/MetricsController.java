package com.example.microproxy.controller;

import com.example.microproxy.dto.ApiResponse;
import com.example.microproxy.dto.PageResponse;
import com.example.microproxy.model.AuditLog;
import com.example.microproxy.service.LoadSimulationService;
import com.example.microproxy.service.MetricsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;
    private final LoadSimulationService loadSimulationService;

    public MetricsController(
            MetricsService metricsService,
            LoadSimulationService loadSimulationService
    ) {
        this.metricsService = metricsService;
        this.loadSimulationService = loadSimulationService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getSummary() {
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Metrics summary retrieved successfully",
                        metricsService.getSummary()
                )
        );
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse> getLogs(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<AuditLog> logs = metricsService.getLogs(service, status, from, to, page, size);

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Logs retrieved successfully",
                        logs
                )
        );
    }

    @PostMapping("/simulate-load")
    public ResponseEntity<ApiResponse> simulateLoad() {
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Load simulation executed successfully",
                        loadSimulationService.simulate(50)
                )
        );
    }
}
