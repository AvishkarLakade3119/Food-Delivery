package com.fooddelivery.order.outbox;

import com.fooddelivery.order.entity.OutboxEvent;
import com.fooddelivery.order.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private static final int MAX_RETRIES = 3;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Runs every 5 seconds to publish pending outbox events
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findByStatusAndRetryCountLessThan("PENDING", MAX_RETRIES);

        for (OutboxEvent event : pendingEvents) {
            try {
                rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), event.getPayload());

                event.setStatus("PUBLISHED");
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);

                log.info("Outbox: Published event {} to {}/{}", event.getEventId(), event.getExchange(),
                        event.getRoutingKey());
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= MAX_RETRIES) {
                    event.setStatus("FAILED");
                    log.error("Outbox: Event {} failed after {} retries: {}", event.getEventId(), MAX_RETRIES,
                            e.getMessage());
                }
                outboxEventRepository.save(event);
            }
        }
    }
}