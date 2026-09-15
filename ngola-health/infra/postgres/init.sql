-- Script Inicial que corre automaticamente quando o container
-- PostgreSQL arranca pela primeira vez

-- Extensões
-- uuid-ossp: permite gerar UUIDs com a função uuid_generate_v4()
-- Todas as tabelas do sistema usarão UUID como chave primária.
-- EX: id UUID primary KEY DEFAULT uuid_generate_v4()

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- pgcrypto: Funções de Criptografia para dados sensíveis
-- Vai permitir encriptar/desencriptar dados diretamente do SQL.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- unaccent: remove acentos em pesquisas de texto
-- Vai permitir que a pesquisa por ex: "jose" encontre "José"

CREATE EXTENSION IF NOT EXISTS "unaccent";

-- Configurações de TimeZone
-- configurar Timezone Local Ex: UTC+1 para Angola
ALTER DATABASE ngolahealth SET timezone TO 'Africa/Luanda';

-- Confirmar a configuração (Aparecerá nos logs do container)
DO $$
BEGIN
    RAISE NOTICE 'Timezone configurado: Africa/Luanda (WAT = UTC+1)';
END
$$