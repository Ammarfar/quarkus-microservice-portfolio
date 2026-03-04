# End-to-End Tutorial: Checkout Service

This guide shows 2 ways to run the app end-to-end:

1. Local app run (Quarkus on host, infra in Docker)
2. Full Docker run (app + infra in Docker Compose)

## Prerequisites

- Docker Desktop (or Docker Engine + Compose)
- Java 17+
- Maven 3.9+
- `curl`

## Reuse Existing Images First

If compatible images already exist on your machine, start with:

```bash
docker compose up -d --pull never
```

This prevents pulling newer tags and reuses local images.

To check locally available image tags:

```bash
docker images --format '{{.Repository}}:{{.Tag}}' | grep -E 'postgres|confluentinc/cp-kafka|provectuslabs/kafka-ui|prom/prometheus|grafana/grafana|maven'
```

## Project Structure

- Service source: `checkout-service/`
- Compose stack: `docker-compose.yml`

---

## Option A: Run App Locally (Infra in Docker)

### 1) Start only infra services

From repository root:

```bash
docker compose up -d --pull never postgres kafka kafka-ui prometheus grafana
```

### 2) Run Quarkus app on host

In a second terminal:

```bash
cd checkout-service
mvn quarkus:dev
```

The app should start on `http://localhost:8080`.

### 3) Verify core endpoints

```bash
curl -s http://localhost:8080/q/health/live
curl -s http://localhost:8080/q/health/ready
curl -s http://localhost:8080/q/metrics | head
```

Optional UIs:

- Swagger: `http://localhost:8080/q/swagger-ui`
- Kafka UI: `http://localhost:8090`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin)

### 4) Produce a checkout event

From repository root:

```bash
docker exec -i checkout-kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic order.checkout.requested
```

Paste one JSON event and press Enter:

```json
{"eventId":"11111111-1111-1111-1111-111111111111","orderId":"ORD-123","userId":"USR-1","items":[{"id":"P1","qty":2,"price":10000}]}
```

Press `Ctrl+D` to exit producer.

### 5) Verify output topics

Completed topic:

```bash
docker exec -i checkout-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order.checkout.completed \
  --from-beginning \
  --timeout-ms 10000
```

Failed topic:

```bash
docker exec -i checkout-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order.checkout.failed \
  --from-beginning \
  --timeout-ms 5000
```

### 6) Stop

- Stop local app with `Ctrl+C` in Quarkus terminal.
- Stop infra:

```bash
docker compose down
```

---

## Option B: Run Everything with Docker Compose

### 1) Start full stack

From repository root:

```bash
docker compose up -d --pull never
```

Services started:

- `checkout-service`
- `postgres`
- `kafka`
- `kafka-ui`
- `prometheus`
- `grafana`

### 2) Verify container status

```bash
docker compose ps
```

### 3) Verify app endpoints

```bash
curl -s http://localhost:8080/q/health/live
curl -s http://localhost:8080/q/health/ready
curl -s http://localhost:8080/q/metrics | head
```

### 4) Produce and consume test event

Use the same producer and consumer commands from Option A.

### 5) Inspect logs

```bash
docker compose logs -f checkout-service
```

Look for lines like `checkout_processed` and `outbox_published`.

### 6) Stop and clean

```bash
docker compose down
```

To also remove volumes:

```bash
docker compose down -v
```

---

## Troubleshooting

- If `checkout-service` is still starting, wait for Maven dependency resolution inside container.
- If readiness is DOWN, check dependencies:

```bash
docker compose logs --tail=200 checkout-service
```

- If Kafka commands fail, ensure broker is healthy:

```bash
docker compose ps
```

- If ports are busy (`8080`, `5432`, `29092`, etc.), stop conflicting local services and restart compose.
