package com.ammar.checkout.infrastructure.health;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.sql.Connection;

@Readiness
@ApplicationScoped
public class DatabaseReadinessCheck implements HealthCheck {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return HealthCheckResponse.up("database");
            }
            return HealthCheckResponse.down("database");
        } catch (Exception exception) {
            return HealthCheckResponse.named("database")
                .down()
                .withData("reason", exception.getMessage())
                .build();
        }
    }
}
