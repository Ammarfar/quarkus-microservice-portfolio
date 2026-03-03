package com.ammar.checkout.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.net.InetSocketAddress;
import java.net.Socket;

@Readiness
@ApplicationScoped
public class KafkaReadinessCheck implements HealthCheck {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @Override
    public HealthCheckResponse call() {
        String firstServer = bootstrapServers.split(",")[0].trim();
        String[] hostAndPort = firstServer.split(":");
        String host = hostAndPort[0];
        int port = Integer.parseInt(hostAndPort[1]);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return HealthCheckResponse.up("kafka");
        } catch (Exception exception) {
            return HealthCheckResponse.named("kafka")
                .down()
                .withData("server", firstServer)
                .withData("reason", exception.getMessage())
                .build();
        }
    }
}
