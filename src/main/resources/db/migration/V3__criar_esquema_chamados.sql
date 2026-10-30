CREATE TABLE setores (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    setor_id BIGINT NOT NULL REFERENCES setores(id)
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    matricula VARCHAR(255) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('SOLICITANTE','TECNICO')),
    setor_id BIGINT REFERENCES setores(id),
    CONSTRAINT ck_usuario_tecnico_setor CHECK (tipo <> 'TECNICO' OR setor_id IS NOT NULL)
);

CREATE TABLE chamados (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    local VARCHAR(255) NOT NULL,
    prioridade VARCHAR(20) NOT NULL CHECK (prioridade IN ('BAIXA','MEDIA','ALTA')),
    status VARCHAR(30) NOT NULL CHECK (status IN ('ABERTO','EM_ATENDIMENTO','RESOLVIDO','CANCELADO')),
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP,
    solucao TEXT,
    solicitante_id BIGINT NOT NULL REFERENCES usuarios(id),
    categoria_id BIGINT NOT NULL REFERENCES categorias(id),
    tecnico_id BIGINT REFERENCES usuarios(id)
);

CREATE TABLE interacoes (
    id BIGSERIAL PRIMARY KEY,
    texto TEXT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    chamado_id BIGINT NOT NULL REFERENCES chamados(id),
    autor_id BIGINT NOT NULL REFERENCES usuarios(id)
);

CREATE INDEX idx_chamados_status ON chamados(status);
CREATE INDEX idx_chamados_abertura ON chamados(data_abertura);
CREATE INDEX idx_interacoes_chamado ON interacoes(chamado_id);
