package com.delivery.paymentservice.dto;

import java.time.LocalDateTime;

public record PaymentErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
