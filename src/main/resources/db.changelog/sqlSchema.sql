CREATE SCHEMA IF NOT EXISTS scheduler;

CREATE TABLE IF NOT EXISTS scheduler.channels
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) UNIQUE NOT NULL,
    description  VARCHAR(140) NOT NULL
);

CREATE TABLE IF NOT EXISTS scheduler.channel_recipients
(
    channel_id SERIAL REFERENCES scheduler.channels (id),
    recipient_address VARCHAR(100) NOT NULL,
    PRIMARY KEY (channel_id, recipient_address)
);

CREATE TABLE IF NOT EXISTS scheduler.mailings
(
    id           SERIAL PRIMARY KEY,
    channel_id   SERIAL REFERENCES scheduler.channels (id),
    subject      VARCHAR(100) NOT NULL,
    text         VARCHAR NOT NULL,
    from_time    TIMESTAMP NULL,
    to_time      TIMESTAMP NULL,
    duration     INTERVAL NULL
);

CREATE TABLE IF NOT EXISTS scheduler.emails
(
    id        SERIAL PRIMARY KEY,
    recipient VARCHAR(100) NOT NULL,
    subject   VARCHAR(100) NULL,
    text      VARCHAR      NOT NULL,
    status    VARCHAR(25) NOT NULL
);