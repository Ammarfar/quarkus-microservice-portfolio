package com.ammar.checkout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_event")
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    public UUID eventId;

    @Column(name = "processed_at", nullable = false)
    public Instant processedAt;
}
