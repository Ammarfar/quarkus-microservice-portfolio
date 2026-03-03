package com.ammar.checkout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class OutboxEventEntity {

    @Id
    public UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 64)
    public String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 128)
    public String aggregateId;

    @Column(name = "event_type", nullable = false, length = 128)
    public String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    public String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    public OutboxStatus status;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "sent_at")
    public Instant sentAt;
}
