CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(180) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT ck_usuario_perfil
        CHECK (perfil IN ('ADMINISTRADOR', 'PESQUISADOR'))
);

CREATE TABLE provedores_ia (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT uk_provedor_ia_nome UNIQUE (nome),
    CONSTRAINT uk_provedor_ia_codigo UNIQUE (codigo)
);

CREATE TABLE criterios_comparacao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    codigo VARCHAR(100) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT uk_criterio_comparacao_nome UNIQUE (nome),
    CONSTRAINT uk_criterio_comparacao_codigo UNIQUE (codigo)
);

CREATE TABLE categorias_classificacao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    criada_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_categoria_classificacao_nome UNIQUE (nome)
);

CREATE TABLE prompts (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    conteudo TEXT NOT NULL,
    versao INTEGER NOT NULL DEFAULT 1,
    ativo BOOLEAN NOT NULL DEFAULT FALSE,
    autor VARCHAR(150) NOT NULL,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ck_prompt_versao_positiva CHECK (versao > 0)
);

CREATE TABLE clientes_helpdesk (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    sistema_origem VARCHAR(150) NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    criado_por_usuario_id BIGINT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cliente_helpdesk_api_key UNIQUE (api_key),
    CONSTRAINT fk_cliente_helpdesk_criado_por_usuario
        FOREIGN KEY (criado_por_usuario_id) REFERENCES usuarios (id)
);

CREATE TABLE modelos_ia (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    provedor_ia_id BIGINT NOT NULL,
    identificador_modelo VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    custo_entrada_por_mil_tokens NUMERIC(12, 6),
    custo_saida_por_mil_tokens NUMERIC(12, 6),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT uk_modelo_ia_provedor_identificador
        UNIQUE (provedor_ia_id, identificador_modelo),
    CONSTRAINT fk_modelo_ia_provedor_ia
        FOREIGN KEY (provedor_ia_id) REFERENCES provedores_ia (id),
    CONSTRAINT ck_modelo_ia_custo_entrada
        CHECK (custo_entrada_por_mil_tokens IS NULL OR custo_entrada_por_mil_tokens >= 0),
    CONSTRAINT ck_modelo_ia_custo_saida
        CHECK (custo_saida_por_mil_tokens IS NULL OR custo_saida_por_mil_tokens >= 0)
);

CREATE TABLE execucoes_teste (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ck_execucao_teste_status
        CHECK (status IN ('PENDENTE', 'EM_EXECUCAO', 'CONCLUIDA', 'FALHA'))
);

CREATE TABLE chamados (
    id BIGSERIAL PRIMARY KEY,
    codigo_externo VARCHAR(150) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    sistema_origem VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'RECEBIDO',
    cliente_helpdesk_id BIGINT NOT NULL,
    categoria_esperada_id BIGINT,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT uk_chamado_cliente_codigo_externo
        UNIQUE (cliente_helpdesk_id, codigo_externo),
    CONSTRAINT fk_chamado_cliente_helpdesk
        FOREIGN KEY (cliente_helpdesk_id) REFERENCES clientes_helpdesk (id),
    CONSTRAINT fk_chamado_categoria_esperada
        FOREIGN KEY (categoria_esperada_id) REFERENCES categorias_classificacao (id),
    CONSTRAINT ck_chamado_status
        CHECK (status IN ('RECEBIDO', 'EM_PROCESSAMENTO', 'CLASSIFICADO', 'ERRO'))
);

CREATE TABLE classificacoes_ia (
    id BIGSERIAL PRIMARY KEY,
    chamado_id BIGINT NOT NULL,
    modelo_ia_id BIGINT NOT NULL,
    prompt_id BIGINT NOT NULL,
    categoria_atribuida_id BIGINT,
    execucao_teste_id BIGINT,
    nivel_confianca NUMERIC(5, 4),
    justificativa TEXT,
    prompt_final TEXT NOT NULL,
    resposta_bruta TEXT,
    tokens_entrada INTEGER,
    tokens_saida INTEGER,
    tempo_resposta_ms BIGINT,
    custo_estimado NUMERIC(14, 6),
    sucesso BOOLEAN NOT NULL DEFAULT FALSE,
    mensagem_erro VARCHAR(1000),
    criada_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_classificacao_ia_chamado
        FOREIGN KEY (chamado_id) REFERENCES chamados (id),
    CONSTRAINT fk_classificacao_ia_modelo_ia
        FOREIGN KEY (modelo_ia_id) REFERENCES modelos_ia (id),
    CONSTRAINT fk_classificacao_ia_prompt
        FOREIGN KEY (prompt_id) REFERENCES prompts (id),
    CONSTRAINT fk_classificacao_ia_categoria_atribuida
        FOREIGN KEY (categoria_atribuida_id) REFERENCES categorias_classificacao (id),
    CONSTRAINT fk_classificacao_ia_execucao_teste
        FOREIGN KEY (execucao_teste_id) REFERENCES execucoes_teste (id),
    CONSTRAINT ck_classificacao_ia_nivel_confianca
        CHECK (nivel_confianca IS NULL OR nivel_confianca BETWEEN 0 AND 1),
    CONSTRAINT ck_classificacao_ia_tokens_entrada
        CHECK (tokens_entrada IS NULL OR tokens_entrada >= 0),
    CONSTRAINT ck_classificacao_ia_tokens_saida
        CHECK (tokens_saida IS NULL OR tokens_saida >= 0),
    CONSTRAINT ck_classificacao_ia_tempo_resposta
        CHECK (tempo_resposta_ms IS NULL OR tempo_resposta_ms >= 0),
    CONSTRAINT ck_classificacao_ia_custo
        CHECK (custo_estimado IS NULL OR custo_estimado >= 0)
);

CREATE TABLE metricas_classificacao (
    id BIGSERIAL PRIMARY KEY,
    classificacao_ia_id BIGINT NOT NULL,
    acertou BOOLEAN NOT NULL,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_metrica_classificacao_classificacao_ia UNIQUE (classificacao_ia_id),
    CONSTRAINT fk_metrica_classificacao_classificacao_ia
        FOREIGN KEY (classificacao_ia_id) REFERENCES classificacoes_ia (id)
);

CREATE TABLE resultados_comparativos (
    id BIGSERIAL PRIMARY KEY,
    chamado_id BIGINT NOT NULL,
    categoria_final_id BIGINT NOT NULL,
    criterio_comparacao_id BIGINT NOT NULL,
    total_modelos INTEGER NOT NULL,
    quantidade_concordante INTEGER NOT NULL,
    percentual_concordancia NUMERIC(5, 2) NOT NULL,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_resultado_comparativo_chamado UNIQUE (chamado_id),
    CONSTRAINT fk_resultado_comparativo_chamado
        FOREIGN KEY (chamado_id) REFERENCES chamados (id),
    CONSTRAINT fk_resultado_comparativo_categoria_final
        FOREIGN KEY (categoria_final_id) REFERENCES categorias_classificacao (id),
    CONSTRAINT fk_resultado_comparativo_criterio_comparacao
        FOREIGN KEY (criterio_comparacao_id) REFERENCES criterios_comparacao (id),
    CONSTRAINT ck_resultado_comparativo_total_modelos CHECK (total_modelos > 0),
    CONSTRAINT ck_resultado_comparativo_quantidade_concordante
        CHECK (quantidade_concordante >= 0 AND quantidade_concordante <= total_modelos),
    CONSTRAINT ck_resultado_comparativo_percentual
        CHECK (percentual_concordancia BETWEEN 0 AND 100)
);

CREATE INDEX idx_chamado_status ON chamados (status);
CREATE INDEX idx_classificacao_ia_chamado ON classificacoes_ia (chamado_id);
CREATE INDEX idx_classificacao_ia_modelo ON classificacoes_ia (modelo_ia_id);
CREATE INDEX idx_classificacao_ia_execucao_teste ON classificacoes_ia (execucao_teste_id);
