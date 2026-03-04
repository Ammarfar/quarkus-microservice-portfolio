package com.ammar.checkout.workflow;

import com.ammar.checkout.service.model.CheckoutRequestedEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class CheckoutSagaRuntimeTest {

    @Inject
    @Named("checkoutMarketplaceSaga")
    Process<? extends Model> checkoutProcess;

    @Test
    void shouldRunCheckoutSagaProcessToCompletion() {
        CheckoutRequestedEvent event = new CheckoutRequestedEvent(
            UUID.randomUUID().toString(),
            "ORDER-RUNTIME-1",
            "USER-1",
            List.of(new CheckoutRequestedEvent.CheckoutItem("P1", 2, new BigDecimal("10000")))
        );

        Model model = checkoutProcess.createModel();
        model.fromMap(Map.of("event", event));

        org.kie.kogito.process.ProcessInstance<?> processInstance = checkoutProcess.createInstance(model);
        processInstance.start();

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
    }
}
