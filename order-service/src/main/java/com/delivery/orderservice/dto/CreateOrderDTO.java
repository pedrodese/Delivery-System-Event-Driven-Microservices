package com.delivery.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDTO(
        @NotBlank(message = "Customer name is required!")
        String customerName,

        @NotBlank(message = "Address is required!")
        String address
) {}
