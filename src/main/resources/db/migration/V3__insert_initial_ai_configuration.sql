INSERT INTO provedores_ia (nome, codigo, descricao, ativo)
VALUES
    ('OpenAI', 'OPENAI', 'Provider OpenAI simulado.', TRUE),
    ('Claude', 'CLAUDE', 'Provider Claude simulado.', TRUE),
    ('DeepSeek', 'DEEPSEEK', 'Provider DeepSeek simulado.', TRUE)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO criterios_comparacao (nome, codigo, descricao, ativo)
VALUES (
    'Maioria simples',
    'MAIORIA',
    'Seleciona a categoria indicada pela maior quantidade de modelos.',
    TRUE
)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO prompts (nome, descricao, conteudo, versao, ativo, autor)
SELECT
    'Classificacao principal',
    'Prompt inicial para classificar chamados em categorias ativas.',
    $prompt$
Voce e um classificador de chamados de Help Desk.

Titulo: {{titulo}}
Descricao: {{descricao}}
Sistema de origem: {{sistema_origem}}
Categorias permitidas: {{categorias}}

Escolha somente uma das categorias permitidas e justifique brevemente.
$prompt$,
    1,
    TRUE,
    'Sistema'
WHERE NOT EXISTS (SELECT 1 FROM prompts WHERE ativo = TRUE);

INSERT INTO modelos_ia (
    nome,
    provedor_ia_id,
    identificador_modelo,
    descricao,
    custo_entrada_por_mil_tokens,
    custo_saida_por_mil_tokens,
    ativo
)
SELECT
    'OpenAI simulado',
    id,
    'openai-simulado',
    'Modelo local simulado para desenvolvimento.',
    0,
    0,
    TRUE
FROM provedores_ia
WHERE codigo = 'OPENAI'
ON CONFLICT (provedor_ia_id, identificador_modelo) DO NOTHING;

INSERT INTO modelos_ia (
    nome,
    provedor_ia_id,
    identificador_modelo,
    descricao,
    custo_entrada_por_mil_tokens,
    custo_saida_por_mil_tokens,
    ativo
)
SELECT
    'Claude simulado',
    id,
    'claude-simulado',
    'Modelo local simulado para desenvolvimento.',
    0,
    0,
    TRUE
FROM provedores_ia
WHERE codigo = 'CLAUDE'
ON CONFLICT (provedor_ia_id, identificador_modelo) DO NOTHING;

INSERT INTO modelos_ia (
    nome,
    provedor_ia_id,
    identificador_modelo,
    descricao,
    custo_entrada_por_mil_tokens,
    custo_saida_por_mil_tokens,
    ativo
)
SELECT
    'DeepSeek simulado',
    id,
    'deepseek-simulado',
    'Modelo local simulado para desenvolvimento.',
    0,
    0,
    TRUE
FROM provedores_ia
WHERE codigo = 'DEEPSEEK'
ON CONFLICT (provedor_ia_id, identificador_modelo) DO NOTHING;
