package dev.ammar.checkout.repository;

import dev.ammar.checkout.entity.ProcessedEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ProcessedEventRepository implements PanacheRepositoryBase<ProcessedEvent, UUID> {

    public boolean isProcessed(UUID eventId) {
        return findByIdOptional(eventId).isPresent();
    }
}
