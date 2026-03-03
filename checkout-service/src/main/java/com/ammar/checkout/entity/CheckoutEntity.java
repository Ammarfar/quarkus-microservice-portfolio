package com.ammar.checkout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "checkout")
public class CheckoutEntity {

    @Id
    public UUID id;

    @Column(name = "order_id", nullable = false, length = 128)
    public String orderId;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
