package com.delivery.orderservice.dto;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path
) {}
