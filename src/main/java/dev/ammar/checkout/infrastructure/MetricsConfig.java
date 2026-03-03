package dev.ammar.checkout.infrastructure;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MetricsConfig {

    private final Counter checkoutProcessedCounter;
    private final Counter checkoutFailedCounter;
    private final Timer checkoutDurationTimer;

    @Inject
    public MetricsConfig(MeterRegistry registry) {
        this.checkoutProcessedCounter = Counter.builder("checkout_processed_total")
                .description("Total number of successfully processed checkouts")
                .register(registry);

        this.checkoutFailedCounter = Counter.builder("checkout_failed_total")
                .description("Total number of failed checkouts")
                .register(registry);

        this.checkoutDurationTimer = Timer.builder("checkout_duration")
                .description("Time taken to process a checkout")
                .register(registry);
    }

    public Counter getCheckoutProcessedCounter() {
        return checkoutProcessedCounter;
    }

    public Counter getCheckoutFailedCounter() {
        return checkoutFailedCounter;
    }

    public Timer getCheckoutDurationTimer() {
        return checkoutDurationTimer;
    }
}
