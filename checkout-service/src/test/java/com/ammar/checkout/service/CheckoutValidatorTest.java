package com.ammar.checkout.service;

import com.ammar.checkout.exception.BusinessValidationException;
import com.ammar.checkout.service.model.CheckoutRequestedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckoutValidatorTest {

    @Test
    void shouldAcceptValidPayload() {
        CheckoutRequestedEvent event = new CheckoutRequestedEvent(
            "7267f5f4-4863-4fc8-9c3a-b7a5ad55ec4e",
            "ORD-123",
            "USR-1",
            List.of(new CheckoutRequestedEvent.CheckoutItem("P1", 2, new BigDecimal("10000")))
        );

        assertDoesNotThrow(() -> CheckoutValidator.validate(event));
    }

    @Test
    void shouldRejectNegativeQuantity() {
        CheckoutRequestedEvent event = new CheckoutRequestedEvent(
            "7267f5f4-4863-4fc8-9c3a-b7a5ad55ec4e",
            "ORD-123",
            "USR-1",
            List.of(new CheckoutRequestedEvent.CheckoutItem("P1", -1, new BigDecimal("10000")))
        );

        assertThrows(BusinessValidationException.class, () -> CheckoutValidator.validate(event));
    }
}
