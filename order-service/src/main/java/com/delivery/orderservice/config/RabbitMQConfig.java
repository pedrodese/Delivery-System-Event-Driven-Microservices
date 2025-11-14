package com.delivery.orderservice.config;

import com.delivery.orderservice.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

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

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(RabbitMQConstants.PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentApprovedQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PAYMENT_APPROVED_QUEUE).build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PAYMENT_FAILED_QUEUE).build();
    }

    @Bean
    public Binding bindingPaymentApproved(Queue paymentApprovedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentApprovedQueue)
                .to(paymentExchange)
                .with(RabbitMQConstants.PAYMENT_ROUTING_KEY_APPROVED);
    }

    @Bean
    public Binding bindingPaymentFailed(Queue paymentFailedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentFailedQueue)
                .to(paymentExchange)
                .with(RabbitMQConstants.PAYMENT_ROUTING_KEY_FAILED);
    }
}