-- Checkout table
CREATE TABLE IF NOT EXISTS checkout (
    id              UUID PRIMARY KEY,
    order_id        VARCHAR(255) NOT NULL,
    total_amount    NUMERIC(19, 2) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_checkout_order_id ON checkout (order_id);

-- Processed event table (idempotency)
CREATE TABLE IF NOT EXISTS processed_event (
    event_id        UUID PRIMARY KEY,
    processed_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Outbox event table (transactional outbox pattern)
CREATE TABLE IF NOT EXISTS outbox_event (
    id              UUID PRIMARY KEY,
    aggregate_type  VARCHAR(255) NOT NULL,
    aggregate_id    VARCHAR(255) NOT NULL,
    event_type      VARCHAR(255) NOT NULL,
    payload         JSONB NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_outbox_event_status ON outbox_event (status);
