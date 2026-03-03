package com.ammar.checkout.repository;

import com.ammar.checkout.entity.CheckoutEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CheckoutRepository implements PanacheRepositoryBase<CheckoutEntity, UUID> {
}
