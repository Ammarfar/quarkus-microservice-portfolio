package com.ammar.checkout.service;

import com.ammar.checkout.entity.CheckoutEntity;
import com.ammar.checkout.entity.OutboxEventEntity;
import com.ammar.checkout.entity.OutboxStatus;
import com.ammar.checkout.entity.ProcessedEventEntity;
import com.ammar.checkout.exception.BusinessValidationException;
import com.ammar.checkout.repository.CheckoutRepository;
import com.ammar.checkout.repository.OutboxRepository;
import com.ammar.checkout.repository.ProcessedEventRepository;
import com.ammar.checkout.service.model.CheckoutRequestedEvent;
import com.ammar.checkout.service.model.CheckoutResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class CheckoutProcessingService {

    @Inject
    ProcessedEventRepository processedEventRepository;

    @Inject
    CheckoutRepository checkoutRepository;

    @Inject
    OutboxRepository outboxRepository;

    @Inject
    InventoryGateway inventoryGateway;

    @Inject
    ObjectMapper objectMapper;

    @Transactional
    public CheckoutResult process(CheckoutRequestedEvent event) throws Exception {
        UUID eventId = parseEventId(event.eventId());
        if (processedEventRepository.exists(eventId)) {
            return new CheckoutResult(eventId, event.orderId(), "DUPLICATE", true, null);
        }

        try {
            CheckoutValidator.validate(event);

            boolean reserved = inventoryGateway.reserve(event);
            if (!reserved) {
                persistFailure(eventId, event.orderId(), "inventory reservation failed");
                return new CheckoutResult(eventId, event.orderId(), "FAILED", false, "inventory reservation failed");
            }

            BigDecimal total = calculateTotal(event);

            CheckoutEntity checkout = new CheckoutEntity();
            checkout.id = UUID.randomUUID();
            checkout.orderId = event.orderId();
            checkout.totalAmount = total;
            checkout.createdAt = Instant.now();
            checkoutRepository.persist(checkout);

            persistProcessed(eventId);
            persistOutbox("order.checkout.completed", event.orderId(), createSuccessPayload(eventId, checkout, event));

            return new CheckoutResult(eventId, event.orderId(), "COMPLETED", false, null);
        } catch (BusinessValidationException exception) {
            persistFailure(eventId, event.orderId(), exception.getMessage());
            return new CheckoutResult(eventId, event.orderId(), "FAILED", false, exception.getMessage());
        }
    }

    private UUID parseEventId(String eventId) {
        try {
            return UUID.fromString(eventId);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid eventId: " + eventId);
        }
    }

    private BigDecimal calculateTotal(CheckoutRequestedEvent event) {
        return event.items().stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.qty())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void persistFailure(UUID eventId, String orderId, String reason) throws Exception {
        persistProcessed(eventId);
        persistOutbox("order.checkout.failed", orderId, createFailurePayload(eventId, orderId, reason));
    }

    private void persistProcessed(UUID eventId) {
        ProcessedEventEntity processedEvent = new ProcessedEventEntity();
        processedEvent.eventId = eventId;
        processedEvent.processedAt = Instant.now();
        processedEventRepository.persist(processedEvent);
    }

    private void persistOutbox(String eventType, String aggregateId, String payload) {
        OutboxEventEntity outboxEvent = new OutboxEventEntity();
        outboxEvent.id = UUID.randomUUID();
        outboxEvent.aggregateType = "checkout";
        outboxEvent.aggregateId = aggregateId;
        outboxEvent.eventType = eventType;
        outboxEvent.payload = payload;
        outboxEvent.status = OutboxStatus.PENDING;
        outboxEvent.createdAt = Instant.now();
        outboxRepository.persist(outboxEvent);
    }

    private String createSuccessPayload(UUID eventId, CheckoutEntity checkout, CheckoutRequestedEvent sourceEvent) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", eventId);
        payload.put("orderId", sourceEvent.orderId());
        payload.put("checkoutId", checkout.id);
        payload.put("userId", sourceEvent.userId());
        payload.put("totalAmount", checkout.totalAmount);
        payload.put("createdAt", checkout.createdAt);
        payload.put("status", "COMPLETED");
        return objectMapper.writeValueAsString(payload);
    }

    private String createFailurePayload(UUID eventId, String orderId, String reason) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", eventId);
        payload.put("orderId", orderId);
        payload.put("status", "FAILED");
        payload.put("reason", reason);
        payload.put("createdAt", Instant.now());
        return objectMapper.writeValueAsString(payload);
    }
}
