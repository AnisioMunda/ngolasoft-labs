--liquibase formatted sql

-- changeset ngolahealth:10

CREATE TABLE token_blacklist (
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);