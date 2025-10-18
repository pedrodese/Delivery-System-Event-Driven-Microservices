package com.delivery.orderservice.event;

import com.delivery.orderservice.constants.RabbitMQConstants;
import com.delivery.orderservice.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(Order order) {
        publish(order, RabbitMQConstants.ORDER_ROUTING_KEY_CREATED);
    }

    public void publishOrderUpdated(Order order) {
        publish(order, RabbitMQConstants.ORDER_ROUTING_KEY_UPDATED);
    }

    public void publishOrderCancelled(Order order) {
        publish(order, RabbitMQConstants.ORDER_ROUTING_KEY_CANCELLED);
    }

    private void publish(Order order, String routingKey) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.ORDER_EXCHANGE,
                routingKey,
                order
        );
    }
}
