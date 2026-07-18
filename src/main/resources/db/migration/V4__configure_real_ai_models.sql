UPDATE provedores_ia
SET descricao = 'Integracao com a API da OpenAI.'
WHERE codigo = 'OPENAI';

UPDATE provedores_ia
SET descricao = 'Integracao com a API da Anthropic Claude.'
WHERE codigo = 'CLAUDE';

UPDATE provedores_ia
SET descricao = 'Integracao com a API da DeepSeek.'
WHERE codigo = 'DEEPSEEK';

UPDATE modelos_ia legado
SET nome = 'OpenAI GPT-5 Mini',
    identificador_modelo = 'gpt-5-mini',
    descricao = 'Modelo real da OpenAI para classificacao de chamados.',
    custo_entrada_por_mil_tokens = 0.000250,
    custo_saida_por_mil_tokens = 0.002000,
    atualizado_em = CURRENT_TIMESTAMP
WHERE legado.identificador_modelo = 'openai-simulado'
  AND NOT EXISTS (
      SELECT 1
      FROM modelos_ia real
      WHERE real.provedor_ia_id = legado.provedor_ia_id
        AND real.identificador_modelo = 'gpt-5-mini'
  );

UPDATE modelos_ia legado
SET ativo = FALSE,
    descricao = 'Modelo simulado legado, substituido pela configuracao real.',
    atualizado_em = CURRENT_TIMESTAMP
WHERE legado.identificador_modelo = 'openai-simulado'
  AND EXISTS (
      SELECT 1
      FROM modelos_ia real
      WHERE real.provedor_ia_id = legado.provedor_ia_id
        AND real.identificador_modelo = 'gpt-5-mini'
  );

UPDATE modelos_ia legado
SET nome = 'Claude Haiku 4.5',
    identificador_modelo = 'claude-haiku-4-5',
    descricao = 'Modelo real da Anthropic para classificacao de chamados.',
    custo_entrada_por_mil_tokens = 0.001000,
    custo_saida_por_mil_tokens = 0.005000,
    atualizado_em = CURRENT_TIMESTAMP
WHERE legado.identificador_modelo = 'claude-simulado'
  AND NOT EXISTS (
      SELECT 1
      FROM modelos_ia real
      WHERE real.provedor_ia_id = legado.provedor_ia_id
        AND real.identificador_modelo = 'claude-haiku-4-5'
  );

UPDATE modelos_ia legado
SET ativo = FALSE,
    descricao = 'Modelo simulado legado, substituido pela configuracao real.',
    atualizado_em = CURRENT_TIMESTAMP
WHERE legado.identificador_modelo = 'claude-simulado'
  AND EXISTS (
      SELECT 1
      FROM modelos_ia real
      WHERE real.provedor_ia_id = legado.provedor_ia_id
        AND real.identificador_modelo = 'claude-haiku-4-5'
  );

UPDATE modelos_ia legado
SET nome = 'DeepSeek V4 Flash',
    identificador_modelo = 'deepseek-v4-flash',
    descricao = 'Modelo real da DeepSeek para classificacao de chamados.',
    custo_entrada_por_mil_tokens = 0.000140,
    custo_saida_por_mil_tokens = 0.000280,
    atualizado_em = CURRENT_TIMESTAMP
WHERE legado.identificador_modelo = 'deepseek-simulado'
  AND NOT EXISTS (
      SELECT 1
      FROM modelos_ia real
      WHERE real.provedor_ia_id = legado.provedor_ia_id
        AND real.identificador_modelo = 'deepseek-v4-flash'
  );

UPDATE modelos_ia legado
SET ativo = FALSE,
    descricao = 'Modelo simulado legado, substituido pela configuracao real.',
    atualizado_em = CURRENT_TIMESTAMP
WHERE legado.identificador_modelo = 'deepseek-simulado'
  AND EXISTS (
      SELECT 1
      FROM modelos_ia real
      WHERE real.provedor_ia_id = legado.provedor_ia_id
        AND real.identificador_modelo = 'deepseek-v4-flash'
  );
