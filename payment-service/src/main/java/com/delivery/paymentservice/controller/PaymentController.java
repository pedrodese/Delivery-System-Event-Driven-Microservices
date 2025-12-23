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
        PaymentResponse paymentCreated = new PaymentResponse(service.processPayment(request));
        URI location = URI.create("/api/v1/payments/" + paymentCreated.orderId());
        return ResponseEntity.created(location).body(paymentCreated);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll() {
        List<PaymentResponse> responseList = service.findAll()
                .stream()
                .map(PaymentResponse::new)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable UUID orderId) {
        PaymentResponse response = new PaymentResponse(service.getByOrderId(orderId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable UUID id) {
        PaymentResponse response = new PaymentResponse(service.refund(id));
        return ResponseEntity.ok(response);
    }
}