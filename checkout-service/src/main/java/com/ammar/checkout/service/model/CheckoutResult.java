package com.ammar.checkout.service.model;

import java.util.UUID;

public record CheckoutResult(
    UUID eventId,
    String orderId,
    String status,
    boolean duplicate,
    String reason
) {
}
