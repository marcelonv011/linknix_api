package br.com.linknix.integration;

import br.com.linknix.exception.IntegracaoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

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

    @Test
    void openAIDeveConsumirRespostaRealEstruturada() throws IOException {
        String respostaApi = """
                {
                  "output": [{
                    "content": [{
                      "type": "output_text",
                      "text": "{\\\"categoria\\\":\\\"DEV\\\",\\\"nivelConfianca\\\":0.97,\\\"justificativa\\\":\\\"Erro de software\\\"}"
                    }]
                  }],
                  "usage": {"input_tokens": 40, "output_tokens": 15}
                }
                """;

        ExecucaoReal execucao = executarComServidorLocal(
                "/responses",
                respostaApi,
                baseUrl -> new OpenAIProvider(
                        new ObjectMapper(),
                        "real",
                        "chave-openai",
                        baseUrl,
                        5
                )
        );

        assertEquals("DEV", execucao.resposta().getCategoriaSugerida());
        assertEquals(40, execucao.resposta().getTokensEntrada());
        assertEquals("Bearer chave-openai", execucao.authorization());
        assertTrue(execucao.corpo().contains("\"json_schema\""));
    }

    @Test
    void claudeDeveConsumirRespostaRealEstruturada() throws IOException {
        String respostaApi = """
                {
                  "content": [{
                    "type": "text",
                    "text": "{\\\"categoria\\\":\\\"SUPORTE\\\",\\\"nivelConfianca\\\":0.91,\\\"justificativa\\\":\\\"Problema de acesso\\\"}"
                  }],
                  "usage": {"input_tokens": 38, "output_tokens": 14}
                }
                """;

        ExecucaoReal execucao = executarComServidorLocal(
                "/messages",
                respostaApi,
                baseUrl -> new ClaudeProvider(
                        new ObjectMapper(),
                        "real",
                        "chave-claude",
                        baseUrl,
                        5
                )
        );

        assertEquals("SUPORTE", execucao.resposta().getCategoriaSugerida());
        assertEquals(14, execucao.resposta().getTokensSaida());
        assertEquals("chave-claude", execucao.apiKeyClaude());
        assertTrue(execucao.corpo().contains("\"output_config\""));
    }

    @Test
    void deepSeekDeveConsumirRespostaRealEmJson() throws IOException {
        String respostaApi = """
                {
                  "choices": [{
                    "message": {
                      "content": "{\\\"categoria\\\":\\\"DEV\\\",\\\"nivelConfianca\\\":0.88,\\\"justificativa\\\":\\\"Falha na aplicacao\\\"}"
                    }
                  }],
                  "usage": {"prompt_tokens": 35, "completion_tokens": 13}
                }
                """;

        ExecucaoReal execucao = executarComServidorLocal(
                "/chat/completions",
                respostaApi,
                baseUrl -> new DeepSeekProvider(
                        new ObjectMapper(),
                        "real",
                        "chave-deepseek",
                        baseUrl,
                        5
                )
        );

        assertEquals("DEV", execucao.resposta().getCategoriaSugerida());
        assertEquals(35, execucao.resposta().getTokensEntrada());
        assertEquals("Bearer chave-deepseek", execucao.authorization());
        assertTrue(execucao.corpo().contains("\"json_object\""));
    }

    private ExecucaoReal executarComServidorLocal(
            String caminho,
            String respostaApi,
            Function<String, LLMProvider> criarProvider
    ) throws IOException {
        AtomicReference<String> corpo = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<String> apiKeyClaude = new AtomicReference<>();
        HttpServer servidor = HttpServer.create(new InetSocketAddress(0), 0);
        servidor.createContext(caminho, exchange -> {
            corpo.set(new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            ));
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            apiKeyClaude.set(exchange.getRequestHeaders().getFirst("x-api-key"));

            byte[] resposta = respostaApi.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, resposta.length);
            exchange.getResponseBody().write(resposta);
            exchange.close();
        });
        servidor.start();

        try {
            String baseUrl = "http://localhost:" + servidor.getAddress().getPort();
            RespostaLLM resposta = criarProvider.apply(baseUrl).executar(solicitacao);
            return new ExecucaoReal(
                    resposta,
                    corpo.get(),
                    authorization.get(),
                    apiKeyClaude.get()
            );
        } finally {
            servidor.stop(0);
        }
    }

    private record ExecucaoReal(
            RespostaLLM resposta,
            String corpo,
            String authorization,
            String apiKeyClaude
    ) {
    }
}
