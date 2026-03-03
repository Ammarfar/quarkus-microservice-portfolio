package dev.ammar.checkout.repository;

import dev.ammar.checkout.entity.OutboxEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxEventRepository implements PanacheRepositoryBase<OutboxEvent, UUID> {

    public List<OutboxEvent> findPending() {
        return list("status", OutboxEvent.OutboxStatus.PENDING);
    }
}
