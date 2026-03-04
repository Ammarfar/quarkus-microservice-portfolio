package com.ammar.checkout.api;

import com.ammar.checkout.service.model.CheckoutRequestedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class CheckoutRequestedConsumer {

    private static final Logger LOG = Logger.getLogger(CheckoutRequestedConsumer.class);

    @Inject
    @Named("checkoutMarketplaceSaga")
    Process<? extends Model> checkoutMarketplaceSaga;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("checkout-requested")
    @Blocking
    public CompletionStage<Void> consume(Message<String> message) {
        try {
            CheckoutRequestedEvent event = objectMapper.readValue(message.getPayload(), CheckoutRequestedEvent.class);
            Model model = checkoutMarketplaceSaga.createModel();
            Map<String, Object> variables = new HashMap<>();
            variables.put("event", event);
            model.fromMap(variables);

            ProcessInstance<?> processInstance = checkoutMarketplaceSaga.createInstance(model);
            processInstance.start();

            LOG.infov("checkout_process_instance_started orderId={0} processInstanceId={1} state={2}",
                event.orderId(), processInstance.id(), processInstance.status());
            return message.ack();
        } catch (Exception exception) {
            LOG.error("checkout processing failed", exception);
            return message.nack(exception);
        }
    }
}
