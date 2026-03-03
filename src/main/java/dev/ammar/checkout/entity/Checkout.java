package dev.ammar.checkout.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "checkout")
public class Checkout extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "order_id", nullable = false)
    public String orderId;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    public Checkout() {
    }

    public Checkout(String orderId, BigDecimal totalAmount) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.createdAt = Instant.now();
    }
}
