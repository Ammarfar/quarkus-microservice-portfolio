# Event-Driven Checkout Service with Transactional Outbox (Quarkus + Kafka)

A production-oriented, event-driven checkout microservice built with Quarkus and Java 17.
Implements idempotent Kafka consumer, transactional service layer, PostgreSQL persistence, and the Outbox pattern to guarantee reliable event publishing. Designed with clean layered architecture and observability.

## Run Tutorial

For a full end-to-end run guide (local and Docker modes), see:

- [docs/end-to-end-tutorial.md](docs/how-to-run.md)
- [docs/kogito-checkout-bpmn.md](docs/kogito-checkout-bpmn.md)

# 1) High Level Architecture

## Checkout Use-case (Business Flow)

```
[Kafka Event] (order.checkout.requested from Order Service)
      ↓
[Consumer]
      ↓
[Service Layer - Transaction]
    ├─ event payload validation
    ├─ check idempotency
    ├─ call inventory service to reserve stock (mocked-up)
    ├─ total calculation
    ├─ insert checkout
    ├─ insert processed_event
    └─ insert outbox
      ↓
[Commit DB]
      ↓
[Outbox Publisher] (background job)
      ↓
[Kafka Publish]
       ↓
[Mark outbox as sent]
```

---

# 2) Tech Stack

| Area          | Tech                                |
| ------------- | ----------------------------------- |
| Framework     | Quarkus 3 + Java 17                 |
| Messaging     | Kafka (SmallRye Reactive Messaging) |
| DB            | PostgreSQL (Hibernate Panache)      |
| Observability | Prometheus + Grafana                |
| Health        | SmallRye Health                     |
| API Docs      | Swagger (OpenAPI Quarkus)           |
| Logging       | JSON logging                        |
| Testing       | JUnit5 + QuarkusTest                |
| Container     | Docker + docker-compose             |

---

# 3) Project Structure (Layered Architecture)

```
checkout-service
│
├── api/              → Kafka Consumer
├── service/          → Business orchestration
├── entity/           → Entity (Panache)
├── repository/       → DB access (Panache)
├── messaging/        → Kafka Producer
├── config/           → Kafka / DB / Metrics config
├── exception/        → Custom exception & mapper
└── infrastructure/   → Logging, metrics, health
```

---

# 4) Domain Modeling

```java
public class Checkout {
    UUID id;
    String orderId;
    BigDecimal totalAmount;
    Instant createdAt;
}
```

---

# 5) Kafka Topics

| Topic                      | Direction |
| -------------------------- | --------- |
| `order.checkout.requested` | consumed  |
| `order.checkout.completed` | produced  |
| `order.checkout.failed`    | produced  |

---

## Incoming event model

```json
{
  "eventId": "uuid",
  "orderId": "ORD-123",
  "userId": "USR-1",
  "items": [
    { "id": "P1", "qty": 2, "price": 10000 }
  ]
}
```

---

## Implementation
* manual ack
* nack on error
* idempotent consumer
* outbox pattern
* DLQ retry strategy

---

## Idempotency Strategy

Gunakan **processed_event table**

```
event_id (PK)
processed_at
```

---

## Outbox Pattern

Gunakan **outbox table**

```
id
aggregate_type
aggregate_id
event_type
payload (jsonb)
status (PENDING/SENT)
created_at
```

---

## DLQ Retry Strategy

Quarkus Kafka config:

```
mp.messaging.incoming.checkout-request.failure-strategy=dead-letter-queue
mp.messaging.incoming.checkout-request.retry-attempts=3
mp.messaging.incoming.checkout-request.retry-delay=5s
```

Failed after retry → goes to DLQ.

---

# 6) Database Timeout

Quarkus datasource config:

```
quarkus.datasource.jdbc.max-size=16
quarkus.datasource.jdbc.acquisition-timeout=5s
quarkus.hibernate-orm.jdbc.statement-timeout=5
```

Prevents hanging DB calls.

---



# 7) Logging Strategy

Use JSON structured logging:

```
quarkus.log.console.json=true
```

Example log:

```json
{
  "event":"checkout_processed",
  "orderId":"ORD-1",
  "duration_ms":120
}
```

---

# 8) Testing Strategy

* e2e testing

---

# 9) Health Check

Quarkus built-in:

```
/q/health/live
/q/health/ready
```

Add Kafka + DB health:

```java
@Readiness
HealthCheck kafka() { }
```

---

# 10) Observability (Prometheus + Grafana)

Expose:

| Metric                          | Example   |
| ------------------------------- | --------- |
| checkout_processed_total        | counter   |
| checkout_failed_total           | counter   |
| checkout_duration               | histogram |
| kafka_consumer_lag              | gauge     |
| db_connection_pool_usage        | gauge     |

Endpoint:

```
/q/metrics
```

---

# 11) Swagger Support

Quarkus supports OpenAPI:

```
/q/swagger-ui
```

Add REST endpoint (optional health API):

```java
@Path("/checkout")
@GET
public String health() {
    return "ok";
}
```

---

# 12) Docker Compose

Services:

```
- checkout-service
- kafka
- kafka-ui
- postgres
- prometheus
- grafana
```

---

# 13) Important Edge Cases

1. Duplicate event delivery
   → solved by processed_events table

2. Out-of-order events
   → use orderId partition key

3. Poison message (always failing)
   → DLQ topic

4. Kafka commit but DB rollback
   → manual ack AFTER transaction

5. Negative qty / zero price
   → validation layer

6. DB slow / connection exhaustion
   → connection pool + timeout

---
