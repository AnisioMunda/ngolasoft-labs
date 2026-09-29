--liquibase formatted sql

-- ============================================================
-- Seed: hospital inicial + role ADMIN + utilizador administrador
-- de teste. Apenas para ambientes de desenvolvimento/homologação.
-- Password de teste: Admin@123  (hash BCrypt, strength 12)
-- ============================================================

--changeset ngolahealth:11
INSERT INTO hospitals (
    id, name, code, hospital_type, province, municipality,
    address, phone, email, active
) VALUES (
    'a1111111-1111-1111-1111-111111111111',
    'Hospital Central de Luanda',
    'HCL-001',
    'PUBLIC',
    'Luanda',
    'Luanda',
    'Rua Principal, Luanda',
    '+244900000000',
    'geral@hospitalcentral.ao',
    TRUE
);

--changeset ngolahealth:12
INSERT INTO roles (id, name, description, active)
VALUES (
    'b1111111-1111-1111-1111-111111111111',
    'ADMIN',
    'Administrador do sistema com acesso total à plataforma',
    TRUE
);

--changeset ngolahealth:13
INSERT INTO permissions (id, name, description) VALUES
    ('d1111111-1111-1111-1111-111111111111', 'hospitals:manage', 'Gerir hospitais na plataforma'),
    ('d2222222-2222-2222-2222-222222222222', 'users:manage',     'Gerir utilizadores e permissões'),
    ('d3333333-3333-3333-3333-333333333333', 'system:admin',     'Acesso administrativo total ao sistema');

--changeset ngolahealth:14
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('b1111111-1111-1111-1111-111111111111', 'd1111111-1111-1111-1111-111111111111'),
    ('b1111111-1111-1111-1111-111111111111', 'd2222222-2222-2222-2222-222222222222'),
    ('b1111111-1111-1111-1111-111111111111', 'd3333333-3333-3333-3333-333333333333');

--changeset ngolahealth:15
-- Password em texto simples: Admin@123
INSERT INTO users (
    id, full_name, username, password_hash, email, phone,
    register_status, must_change_password, hospital_id
) VALUES (
    'c1111111-1111-1111-1111-111111111111',
    'Administrador do Sistema',
    'admin.system',
    '$2b$12$lgi38E9MK4iX6JCxK/yUAOYonHNaYpdnBmBhSK.4NF0yYtLfZwele',
    'admin@hospitalcentral.ao',
    '+244900000001',
    'ACTIVE',
    FALSE,
    'a1111111-1111-1111-1111-111111111111'
);

--changeset ngolahealth:16
INSERT INTO user_roles (user_id, role_id) VALUES (
    'c1111111-1111-1111-1111-111111111111',
    'b1111111-1111-1111-1111-111111111111'
);