CREATE TABLE IF NOT EXISTS checkout (
    id UUID PRIMARY KEY,
    order_id VARCHAR(128) NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_event (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id VARCHAR(128) NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_outbox_status_created_at ON outbox(status, created_at);
