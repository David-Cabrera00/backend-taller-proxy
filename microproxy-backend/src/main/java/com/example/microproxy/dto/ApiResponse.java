package com.example.microproxy.dto;

public record ApiResponse(
        boolean success,
        String message,
        Object data
) {
}
