package com.delivery.orderservice.mapper;

import com.delivery.orderservice.dto.CreateOrderDTO;
import com.delivery.orderservice.dto.OrderResponseDTO;
import com.delivery.orderservice.model.Order;
import org.aspectj.weaver.ast.Or;

public class OrderMapper {

    public static Order toEntity(CreateOrderDTO dto) {
        Order entity = new Order();
        entity.setCustomerName(dto.customerName());
        entity.setAddress(dto.address());
        return entity;
    }

    public static OrderResponseDTO toDTO(Order entity) {
        return new OrderResponseDTO(
                entity.getId(),
                entity.getCustomerName(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

}
