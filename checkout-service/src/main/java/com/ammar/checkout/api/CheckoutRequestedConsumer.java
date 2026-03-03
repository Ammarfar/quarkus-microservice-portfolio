package com.ammar.checkout.api;

import com.ammar.checkout.service.CheckoutProcessingService;
import com.ammar.checkout.service.model.CheckoutRequestedEvent;
import com.ammar.checkout.service.model.CheckoutResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class CheckoutRequestedConsumer {

    private static final Logger LOG = Logger.getLogger(CheckoutRequestedConsumer.class);

    @Inject
    CheckoutProcessingService checkoutProcessingService;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("checkout-requested")
    public CompletionStage<Void> consume(Message<String> message) {
        try {
            CheckoutRequestedEvent event = objectMapper.readValue(message.getPayload(), CheckoutRequestedEvent.class);
            CheckoutResult result = checkoutProcessingService.process(event);
            LOG.infov("checkout_processed orderId={0} status={1} duplicate={2}", result.orderId(), result.status(), result.duplicate());
            return message.ack();
        } catch (Exception exception) {
            LOG.error("checkout processing failed", exception);
            return message.nack(exception);
        }
    }
}
