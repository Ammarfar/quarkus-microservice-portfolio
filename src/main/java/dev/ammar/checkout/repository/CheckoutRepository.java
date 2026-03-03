package dev.ammar.checkout.repository;

import dev.ammar.checkout.entity.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CheckoutRepository implements PanacheRepositoryBase<Checkout, UUID> {

    public Optional<Checkout> findByOrderId(String orderId) {
        return find("orderId", orderId).firstResultOptional();
    }
}
