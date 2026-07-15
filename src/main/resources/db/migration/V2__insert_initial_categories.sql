INSERT INTO categorias_classificacao (nome, descricao, ativa)
VALUES
    (
        'DEV',
        'Chamados que exigem análise ou alteração no código-fonte da aplicação.',
        TRUE
    ),
    (
        'SUPORTE',
        'Chamados que podem ser resolvidos por orientação, configuração ou atendimento técnico.',
        TRUE
    )
ON CONFLICT (nome) DO NOTHING;
