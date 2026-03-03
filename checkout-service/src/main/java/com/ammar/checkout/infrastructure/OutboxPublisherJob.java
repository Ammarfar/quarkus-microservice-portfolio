package com.ammar.checkout.infrastructure;

import com.ammar.checkout.entity.OutboxEventEntity;
import com.ammar.checkout.entity.OutboxStatus;
import com.ammar.checkout.messaging.CheckoutEventPublisher;
import com.ammar.checkout.repository.OutboxRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class OutboxPublisherJob {

    private static final Logger LOG = Logger.getLogger(OutboxPublisherJob.class);

    @Inject
    OutboxRepository outboxRepository;

    @Inject
    CheckoutEventPublisher checkoutEventPublisher;

    @Scheduled(every = "5s", delayed = "5s")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findPending(50);
        for (OutboxEventEntity pendingEvent : pendingEvents) {
            try {
                checkoutEventPublisher.publish(pendingEvent.eventType, pendingEvent.payload);
                pendingEvent.status = OutboxStatus.SENT;
                pendingEvent.sentAt = Instant.now();
                LOG.infov("outbox_published id={0} type={1}", pendingEvent.id, pendingEvent.eventType);
            } catch (Exception exception) {
                LOG.warnv(exception, "outbox_publish_failed id={0} type={1}", pendingEvent.id, pendingEvent.eventType);
            }
        }
    }
}
