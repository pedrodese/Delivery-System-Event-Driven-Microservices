package com.delivery.orderservice.controller;

import com.delivery.orderservice.dto.CreateOrderDTO;
import com.delivery.orderservice.dto.OrderResponseDTO;
import com.delivery.orderservice.enums.OrderStatus;
import com.delivery.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody CreateOrderDTO dto) {
        OrderResponseDTO createdOrder = new OrderResponseDTO(service.create(dto));
        URI location = URI.create("/orders/" + createdOrder.id());
        return ResponseEntity.created(location).body(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findAll() {
        List<OrderResponseDTO> response = service.findAll()
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable UUID id) {
        OrderResponseDTO response = new OrderResponseDTO(service.findById(id));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status
    ) {
        OrderResponseDTO response = new OrderResponseDTO(service.updateStatus(id, status));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }
}

