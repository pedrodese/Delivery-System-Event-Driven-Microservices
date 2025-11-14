package com.delivery.paymentservice.config;

import com.delivery.paymentservice.constants.RabbitMQConstants;
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
    public Queue paymentRefundedQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PAYMENT_REFUNDED_QUEUE).build();
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

    @Bean
    public Binding bindingPaymentRefunded(Queue paymentRefundedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentRefundedQueue)
                .to(paymentExchange)
                .with(RabbitMQConstants.PAYMENT_ROUTING_KEY_REFUNDED);
    }