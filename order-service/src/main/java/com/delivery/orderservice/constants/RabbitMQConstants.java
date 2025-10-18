package com.delivery.orderservice.constants;

public class RabbitMQConstants {

    public static final String ORDER_EXCHANGE = "order.exchange";

    // Routing keys
    public static final String ORDER_ROUTING_KEY_CREATED = "order.created";
    public static final String ORDER_ROUTING_KEY_UPDATED = "order.updated";
    public static final String ORDER_ROUTING_KEY_CANCELLED = "order.cancelled";

    // Filas
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_UPDATED_QUEUE = "order.updated.queue";
    public static final String ORDER_CANCELLED_QUEUE = "order.cancelled.queue";

    private RabbitMQConstants() {} // impede instância
}
