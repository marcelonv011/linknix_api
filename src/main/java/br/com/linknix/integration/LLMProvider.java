package br.com.linknix.integration;

public interface LLMProvider {

    String obterCodigo();

    RespostaLLM executar(SolicitacaoLLM solicitacao);
}
