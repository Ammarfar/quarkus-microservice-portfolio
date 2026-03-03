package com.ammar.checkout.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CheckoutEventPublisher {

    @Inject
    @Channel("checkout-completed")
    Emitter<String> completedEmitter;

    @Inject
    @Channel("checkout-failed")
    Emitter<String> failedEmitter;

    public void publish(String eventType, String payload) throws Exception {
        CompletionStage<Void> sendResult;

        if ("order.checkout.completed".equals(eventType)) {
            sendResult = completedEmitter.send(payload);
        } else if ("order.checkout.failed".equals(eventType)) {
            sendResult = failedEmitter.send(payload);
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + eventType);
        }

        sendResult.toCompletableFuture().get(10, TimeUnit.SECONDS);
    }
}
