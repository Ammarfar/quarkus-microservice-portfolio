package com.ammar.checkout.infrastructure;

import com.ammar.checkout.service.InventoryGateway;
import com.ammar.checkout.service.model.CheckoutRequestedEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockInventoryGateway implements InventoryGateway {

    @Override
    public boolean reserve(CheckoutRequestedEvent event) {
        return true;
    }
}
