package com.ammar.checkout.repository;

import com.ammar.checkout.entity.OutboxEventEntity;
import com.ammar.checkout.entity.OutboxStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxRepository implements PanacheRepositoryBase<OutboxEventEntity, UUID> {

    public List<OutboxEventEntity> findPending(int maxItems) {
        return find("status = ?1 order by createdAt", OutboxStatus.PENDING)
            .page(0, maxItems)
            .list();
    }
}
