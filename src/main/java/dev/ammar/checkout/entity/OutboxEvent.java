package dev.ammar.checkout.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "aggregate_type", nullable = false)
    public String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    public String aggregateId;

    @Column(name = "event_type", nullable = false)
    public String eventType;

    @Column(nullable = false, columnDefinition = "jsonb")
    public String payload;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    public OutboxStatus status;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    public OutboxEvent() {
    }

    public OutboxEvent(String aggregateType, String aggregateId,
                       String eventType, String payload) {
        this.id = UUID.randomUUID();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public enum OutboxStatus {
        PENDING,
        SENT
    }
}
