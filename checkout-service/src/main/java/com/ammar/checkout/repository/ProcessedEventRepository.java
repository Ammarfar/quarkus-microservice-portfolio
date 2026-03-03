package com.ammar.checkout.repository;

import com.ammar.checkout.entity.ProcessedEventEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProcessedEventRepository implements PanacheRepositoryBase<ProcessedEventEntity, UUID> {

    public boolean exists(UUID eventId) {
        return findById(eventId) != null;
    }
}
