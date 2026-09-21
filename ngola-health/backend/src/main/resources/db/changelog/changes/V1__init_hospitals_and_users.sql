--liquibase formatted sql

--changeset ngolahealth:1
CREATE TABLE hospitals (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    hospital_type VARCHAR(50) NOT NULL,
    province VARCHAR(100) NOT NULL,
    municipality VARCHAR(100),
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset ngolahealth:2
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset ngolahealth:3
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset ngolahealth:4
CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

--changeset ngolahealth:5
CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(200) UNIQUE,
    phone VARCHAR(20),
    especiality VARCHAR(100),
    professional_card_number VARCHAR(50),
    register_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMPTZ,
    hospital_id UUID REFERENCES hospitals(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset ngolahealth:6
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

--changeset ngolahealth:7
CREATE INDEX idx_users_hospital_id ON users(hospital_id);

--changeset ngolahealth:8
CREATE INDEX idx_users_register_status ON users(register_status);

--changeset ngolahealth:9
CREATE INDEX idx_hospitals_province ON hospitals(province);