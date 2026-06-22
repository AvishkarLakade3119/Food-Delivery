package com.fooddelivery.restaurant.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ Configuration for Restaurant Service
 * Defines exchanges, queues, bindings, and message converter
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queues.durable:false}")
    private boolean queuesDurable;

    @Value("${rabbitmq.exchanges.durable:false}")
    private boolean exchangesDurable;

    // ========== EXCHANGES ==========
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String RESTAURANT_EXCHANGE = "restaurant.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange";

    // ========== QUEUES ==========
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_CONFIRMED_QUEUE = "order.confirmed.queue";
    public static final String ORDER_CANCELLED_QUEUE = "order.cancelled.queue";
    public static final String PAYMENT_SUCCESS_QUEUE = "payment.success.queue";
    public static final String PAYMENT_FAILED_QUEUE = "payment.failed.queue";
    public static final String RESTAURANT_ACCEPTED_QUEUE = "restaurant.accepted.queue";
    public static final String RESTAURANT_REJECTED_QUEUE = "restaurant.rejected.queue";

    // ========== ROUTING KEYS ==========
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_CONFIRMED_KEY = "order.confirmed";
    public static final String ORDER_CANCELLED_KEY = "order.cancelled";
    public static final String PAYMENT_SUCCESS_KEY = "payment.success";
    public static final String PAYMENT_FAILED_KEY = "payment.failed";
    public static final String RESTAURANT_ACCEPTED_KEY = "restaurant.accepted";
    public static final String RESTAURANT_REJECTED_KEY = "restaurant.rejected";
    public static final String NOTIFICATION_ORDER_KEY = "notification.order";

    // ========== DEAD LETTER QUEUES ==========
    public static final String ORDER_CREATED_DLQ = "order.created.dlq";
    public static final String PAYMENT_SUCCESS_DLQ = "payment.success.dlq";
    public static final String PAYMENT_FAILED_DLQ = "payment.failed.dlq";
    public static final String RESTAURANT_ACCEPTED_DLQ = "restaurant.accepted.dlq";
    public static final String RESTAURANT_REJECTED_DLQ = "restaurant.rejected.dlq";

    // ========== EXCHANGE BEANS ==========
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, exchangesDurable, false);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE, exchangesDurable, false);
    }

    @Bean
    public TopicExchange restaurantExchange() {
        return new TopicExchange(RESTAURANT_EXCHANGE, exchangesDurable, false);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, exchangesDurable, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, exchangesDurable, false);
    }

    // ========== QUEUE BEANS WITH DLX ==========
    @Bean
    public Queue orderCreatedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_CREATED_DLQ);
        return new Queue(ORDER_CREATED_QUEUE, queuesDurable, false, false, args);
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue(ORDER_CONFIRMED_QUEUE, queuesDurable, false, false);
    }

    @Bean
    public Queue orderCancelledQueue() {
        return new Queue(ORDER_CANCELLED_QUEUE, queuesDurable, false, false);
    }

    @Bean
    public Queue paymentSuccessQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", PAYMENT_SUCCESS_DLQ);
        return new Queue(PAYMENT_SUCCESS_QUEUE, queuesDurable, false, false, args);
    }

    @Bean
    public Queue paymentFailedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", PAYMENT_FAILED_DLQ);
        return new Queue(PAYMENT_FAILED_QUEUE, queuesDurable, false, false, args);
    }

    @Bean
    public Queue restaurantAcceptedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", RESTAURANT_ACCEPTED_DLQ);
        return new Queue(RESTAURANT_ACCEPTED_QUEUE, queuesDurable, false, false, args);
    }

    @Bean
    public Queue restaurantRejectedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", RESTAURANT_REJECTED_DLQ);
        return new Queue(RESTAURANT_REJECTED_QUEUE, queuesDurable, false, false, args);
    }

    // ========== DEAD LETTER QUEUES ==========
    @Bean
    public Queue orderCreatedDLQ() {
        return new Queue(ORDER_CREATED_DLQ, true, false, false);
    }

    @Bean
    public Queue paymentSuccessDLQ() {
        return new Queue(PAYMENT_SUCCESS_DLQ, true, false, false);
    }

    @Bean
    public Queue paymentFailedDLQ() {
        return new Queue(PAYMENT_FAILED_DLQ, true, false, false);
    }

    @Bean
    public Queue restaurantAcceptedDLQ() {
        return new Queue(RESTAURANT_ACCEPTED_DLQ, true, false, false);
    }

    @Bean
    public Queue restaurantRejectedDLQ() {
        return new Queue(RESTAURANT_REJECTED_DLQ, true, false, false);
    }

    // ========== BINDINGS ==========
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_KEY);
    }

    @Bean
    public Binding orderConfirmedBinding() {
        return BindingBuilder.bind(orderConfirmedQueue())
                .to(orderExchange())
                .with(ORDER_CONFIRMED_KEY);
    }

    @Bean
    public Binding orderCancelledBinding() {
        return BindingBuilder.bind(orderCancelledQueue())
                .to(orderExchange())
                .with(ORDER_CANCELLED_KEY);
    }

    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder.bind(paymentSuccessQueue())
                .to(paymentExchange())
                .with(PAYMENT_SUCCESS_KEY);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(paymentFailedQueue())
                .to(paymentExchange())
                .with(PAYMENT_FAILED_KEY);
    }

    @Bean
    public Binding restaurantAcceptedBinding() {
        return BindingBuilder.bind(restaurantAcceptedQueue())
                .to(restaurantExchange())
                .with(RESTAURANT_ACCEPTED_KEY);
    }

    @Bean
    public Binding restaurantRejectedBinding() {
        return BindingBuilder.bind(restaurantRejectedQueue())
                .to(restaurantExchange())
                .with(RESTAURANT_REJECTED_KEY);
    }

    // ========== DLQ BINDINGS ==========
    @Bean
    public Binding orderCreatedDLQBinding() {
        return BindingBuilder.bind(orderCreatedDLQ())
                .to(deadLetterExchange())
                .with(ORDER_CREATED_DLQ);
    }

    @Bean
    public Binding paymentSuccessDLQBinding() {
        return BindingBuilder.bind(paymentSuccessDLQ())
                .to(deadLetterExchange())
                .with(PAYMENT_SUCCESS_DLQ);
    }

    @Bean
    public Binding paymentFailedDLQBinding() {
        return BindingBuilder.bind(paymentFailedDLQ())
                .to(deadLetterExchange())
                .with(PAYMENT_FAILED_DLQ);
    }

    @Bean
    public Binding restaurantAcceptedDLQBinding() {
        return BindingBuilder.bind(restaurantAcceptedDLQ())
                .to(deadLetterExchange())
                .with(RESTAURANT_ACCEPTED_DLQ);
    }

    @Bean
    public Binding restaurantRejectedDLQBinding() {
        return BindingBuilder.bind(restaurantRejectedDLQ())
                .to(deadLetterExchange())
                .with(RESTAURANT_REJECTED_DLQ);
    }

    // ========== MESSAGE CONVERTER ==========
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
