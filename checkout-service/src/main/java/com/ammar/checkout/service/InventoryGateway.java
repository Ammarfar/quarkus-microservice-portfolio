package com.ammar.checkout.service;

import com.ammar.checkout.service.model.CheckoutRequestedEvent;

public interface InventoryGateway {

    boolean reserve(CheckoutRequestedEvent event);
}
