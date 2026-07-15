package br.com.linknix.integration;

import br.com.linknix.exception.IntegracaoException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LLMProviderTest {

    private final SolicitacaoLLM solicitacao = SolicitacaoLLM.builder()
            .identificadorModelo("modelo-simulado")
            .promptFinal("Classifique este chamado usando uma categoria válida")
            .categoriasDisponiveis(List.of("DEV", "SUPORTE"))
            .build();

    @Test
    void todosOsProvedoresDevemRetornarUmaCategoriaDisponivel() {
        List<LLMProvider> providers = List.of(
                new OpenAIProvider(),
                new ClaudeProvider(),
                new DeepSeekProvider()
        );

        for (LLMProvider provider : providers) {
            RespostaLLM resposta = provider.executar(solicitacao);

            assertTrue(
                    solicitacao.getCategoriasDisponiveis()
                            .contains(resposta.getCategoriaSugerida())
            );
            assertEquals(provider.obterCodigo(), resposta.getCodigoProvedor());
            assertTrue(resposta.getTokensEntrada() > 0);
            assertTrue(resposta.getTokensSaida() > 0);
        }
    }

    @Test
    void deveProduzirRespostaSimuladaDeterministica() {
        LLMProvider provider = new OpenAIProvider();

        RespostaLLM primeiraResposta = provider.executar(solicitacao);
        RespostaLLM segundaResposta = provider.executar(solicitacao);

        assertEquals(
                primeiraResposta.getCategoriaSugerida(),
                segundaResposta.getCategoriaSugerida()
        );
        assertEquals(
                primeiraResposta.getRespostaBruta(),
                segundaResposta.getRespostaBruta()
        );
    }

    @Test
    void registryDeveLocalizarProvedorIgnorandoMaiusculasEMinusculas() {
        LLMProviderRegistry registry = new LLMProviderRegistry(
                List.of(new OpenAIProvider(), new ClaudeProvider(), new DeepSeekProvider())
        );

        assertEquals("OPENAI", registry.obter(" openai ").obterCodigo());
        assertEquals("CLAUDE", registry.obter("Claude").obterCodigo());
        assertEquals("DEEPSEEK", registry.obter("deepseek").obterCodigo());
    }

    @Test
    void registryDeveRejeitarProvedorSemImplementacao() {
        LLMProviderRegistry registry = new LLMProviderRegistry(
                List.of(new OpenAIProvider())
        );

        assertThrows(
                IntegracaoException.class,
                () -> registry.obter("PROVEDOR_INEXISTENTE")
        );
    }

    @Test
    void providerDeveRejeitarListaDeCategoriasVazia() {
        SolicitacaoLLM solicitacaoSemCategorias = SolicitacaoLLM.builder()
                .identificadorModelo("modelo-simulado")
                .promptFinal("Classifique este chamado")
                .categoriasDisponiveis(List.of())
                .build();

        assertThrows(
                IntegracaoException.class,
                () -> new OpenAIProvider().executar(solicitacaoSemCategorias)
        );
    }
}
