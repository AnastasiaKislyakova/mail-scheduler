CREATE SCHEMA IF NOT EXISTS scheduler;

CREATE TABLE  IF NOT EXISTS scheduler.channels
(
    id           integer NOT NULL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(140) NOT NULL
);

CREATE TABLE  IF NOT EXISTS scheduler.channel_recipients
(
    channel_id integer NOT NULL REFERENCES scheduler.channels (id),
    recipient_address VARCHAR(100) NOT NULL,
    PRIMARY KEY (channel_id, recipient_address)
);

CREATE TABLE  IF NOT EXISTS scheduler.mailings
(
    id           integer NOT NULL PRIMARY KEY,
    channel_id   integer NOT NULL REFERENCES scheduler.channels (id),
    subject      VARCHAR(100) NOT NULL,
    text         VARCHAR,
    from_time    TIMESTAMP NULL,
    to_time      TIMESTAMP NULL,
    duration     INTERVAL NULL
);

CREATE TYPE email_status AS ENUM ('Created', 'Sent', 'Received');

CREATE TABLE  IF NOT EXISTS scheduler.emails
(
    id           integer NOT NULL PRIMARY KEY,
    recipient    VARCHAR(100) NOT NULL,
    subject      VARCHAR(100) NULL,
    text         VARCHAR NOT NULL,
    status      email_status NOT NULL
)