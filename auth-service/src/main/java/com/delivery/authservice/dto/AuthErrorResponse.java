package com.delivery.authservice.dto;

import java.time.LocalDateTime;

public record AuthErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}