package com.delivery.orderservice.config;

import com.delivery.orderservice.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(RabbitMQConstants.ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(RabbitMQConstants.ORDER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return QueueBuilder.durable(RabbitMQConstants.ORDER_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue orderCancelledQueue() {
        return QueueBuilder.durable(RabbitMQConstants.ORDER_CANCELLED_QUEUE).build();
    }

    @Bean
    public Binding bindingCreated(Queue orderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(orderExchange)
                .with(RabbitMQConstants.ORDER_ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingUpdated(Queue orderUpdatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderUpdatedQueue)
                .to(orderExchange)
                .with(RabbitMQConstants.ORDER_ROUTING_KEY_UPDATED);
    }

    @Bean
    public Binding bindingCancelled(Queue orderCancelledQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCancelledQueue)
                .to(orderExchange)
                .with(RabbitMQConstants.ORDER_ROUTING_KEY_CANCELLED);
    }
}
