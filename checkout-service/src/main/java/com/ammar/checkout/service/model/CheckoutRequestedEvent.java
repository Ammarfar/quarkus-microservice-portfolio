package com.ammar.checkout.service.model;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutRequestedEvent(
    String eventId,
    String orderId,
    String userId,
    List<CheckoutItem> items
) {

    public record CheckoutItem(String id, Integer qty, BigDecimal price) {
    }
}
