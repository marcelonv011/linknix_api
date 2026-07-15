package br.com.linknix.integration;

import br.com.linknix.exception.IntegracaoException;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractLLMProviderSimulado implements LLMProvider {

    private static final int CARACTERES_POR_TOKEN_SIMULADO = 4;

    private final String codigo;
    private final BigDecimal nivelConfianca;
    private final long tempoRespostaMs;

    protected AbstractLLMProviderSimulado(
            String codigo,
            BigDecimal nivelConfianca,
            long tempoRespostaMs
    ) {
        this.codigo = codigo;
        this.nivelConfianca = nivelConfianca;
        this.tempoRespostaMs = tempoRespostaMs;
    }

    @Override
    public String obterCodigo() {
        return codigo;
    }

    @Override
    public RespostaLLM executar(SolicitacaoLLM solicitacao) {
        validar(solicitacao);

        List<String> categorias = normalizarCategorias(
                solicitacao.getCategoriasDisponiveis()
        );

        if (categorias.isEmpty()) {
            throw new IntegracaoException(
                    "Nenhuma categoria ativa foi informada ao provedor " + codigo
            );
        }

        int indiceCategoria = Math.floorMod(
                (solicitacao.getPromptFinal() + codigo).hashCode(),
                categorias.size()
        );
        String categoriaSugerida = categorias.get(indiceCategoria);
        String justificativa = "Resposta simulada do provedor " + codigo
                + " para validar o fluxo de classificação.";
        String respostaBruta = "categoria=" + categoriaSugerida
                + "; nivelConfianca=" + nivelConfianca
                + "; justificativa=" + justificativa;

        return RespostaLLM.builder()
                .codigoProvedor(codigo)
                .identificadorModelo(solicitacao.getIdentificadorModelo().trim())
                .categoriaSugerida(categoriaSugerida)
                .nivelConfianca(nivelConfianca)
                .justificativa(justificativa)
                .respostaBruta(respostaBruta)
                .tokensEntrada(estimarTokens(solicitacao.getPromptFinal()))
                .tokensSaida(estimarTokens(respostaBruta))
                .tempoRespostaMs(tempoRespostaMs)
                .build();
    }

    private void validar(SolicitacaoLLM solicitacao) {
        if (solicitacao == null) {
            throw new IntegracaoException("A solicitação ao provedor não pode ser nula");
        }

        if (solicitacao.getIdentificadorModelo() == null
                || solicitacao.getIdentificadorModelo().isBlank()) {
            throw new IntegracaoException("O identificador do modelo é obrigatório");
        }

        if (solicitacao.getPromptFinal() == null
                || solicitacao.getPromptFinal().isBlank()) {
            throw new IntegracaoException("O prompt final é obrigatório");
        }

        if (solicitacao.getCategoriasDisponiveis() == null) {
            throw new IntegracaoException("As categorias disponíveis são obrigatórias");
        }
    }

    private List<String> normalizarCategorias(List<String> categorias) {
        return categorias.stream()
                .filter(categoria -> categoria != null && !categoria.isBlank())
                .map(String::trim)
                .distinct()
                .sorted()
                .toList();
    }

    private int estimarTokens(String texto) {
        return Math.max(
                1,
                (int) Math.ceil((double) texto.length() / CARACTERES_POR_TOKEN_SIMULADO)
        );
    }
}
