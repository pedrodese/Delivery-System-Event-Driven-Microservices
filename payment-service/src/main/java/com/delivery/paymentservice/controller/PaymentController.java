package com.delivery.paymentservice.controller;

import com.delivery.paymentservice.dto.PaymentResponse;
import com.delivery.paymentservice.dto.ProcessPaymentRequest;
import com.delivery.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        PaymentResponse payment = service.processPayment(request);
        URI location = URI.create("/api/v1/payments/" + payment.orderId());
        return ResponseEntity.created(location).body(payment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(service.getByOrderId(orderId));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable UUID id) {
        return ResponseEntity.ok(service.refund(id));
    }
}