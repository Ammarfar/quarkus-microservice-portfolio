package com.ammar.checkout.service;

import com.ammar.checkout.exception.BusinessValidationException;
import com.ammar.checkout.service.model.CheckoutRequestedEvent;

public final class CheckoutValidator {

    private CheckoutValidator() {
    }

    public static void validate(CheckoutRequestedEvent event) {
        if (event == null) {
            throw new BusinessValidationException("Event payload is null");
        }
        if (isBlank(event.orderId())) {
            throw new BusinessValidationException("orderId is required");
        }
        if (isBlank(event.userId())) {
            throw new BusinessValidationException("userId is required");
        }
        if (event.items() == null || event.items().isEmpty()) {
            throw new BusinessValidationException("items must not be empty");
        }

        for (CheckoutRequestedEvent.CheckoutItem item : event.items()) {
            if (item == null || isBlank(item.id())) {
                throw new BusinessValidationException("item id is required");
            }
            if (item.qty() == null || item.qty() <= 0) {
                throw new BusinessValidationException("item qty must be greater than zero");
            }
            if (item.price() == null || item.price().signum() <= 0) {
                throw new BusinessValidationException("item price must be greater than zero");
            }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
