package br.com.linknix.integration;

import br.com.linknix.exception.IntegracaoException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeepSeekProvider extends AbstractLLMProvider {

    private static final String INSTRUCAO_FORMATO = """

            Responda somente como objeto JSON com os campos:
            categoria e justificativa.
            """;

    private final int maxOutputTokens;

    public DeepSeekProvider() {
        super("DEEPSEEK", 100L);
        this.maxOutputTokens = 500;
    }

    @Autowired
    public DeepSeekProvider(
            ObjectMapper objectMapper,
            @Value("${linknix.ia.modo:simulado}") String modo,
            @Value("${linknix.ia.deepseek.api-key:}") String apiKey,
            @Value("${linknix.ia.deepseek.base-url:https://api.deepseek.com}") String baseUrl,
            @Value("${linknix.ia.timeout-segundos:60}") long timeoutSegundos,
            @Value("${linknix.ia.max-output-tokens:500}") int maxOutputTokens
    ) {
        super(
                "DEEPSEEK",
                100L,
                objectMapper,
                modo,
                apiKey,
                baseUrl,
                timeoutSegundos
        );
        this.maxOutputTokens = limitarTokensSaida(maxOutputTokens);
    }

    @Override
    protected RespostaLLM executarReal(SolicitacaoLLM solicitacao) {
        ObjectNode corpo = objectMapper().createObjectNode();
        corpo.put("model", solicitacao.getIdentificadorModelo());
        corpo.put("max_tokens", maxOutputTokens);
        corpo.putArray("messages")
                .addObject()
                .put("role", "user")
                .put("content", solicitacao.getPromptFinal() + INSTRUCAO_FORMATO);
        corpo.putObject("response_format").put("type", "json_object");
        corpo.putObject("thinking").put("type", "disabled");

        long inicio = System.nanoTime();
        JsonNode resposta = enviarPost(
                "/chat/completions",
                corpo,
                Map.of("Authorization", "Bearer " + apiKey())
        );
        long tempoMs = (System.nanoTime() - inicio) / 1_000_000;

        String conteudo = resposta.path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();
        if (conteudo.isBlank()) {
            throw new IntegracaoException("A DeepSeek nao retornou texto na resposta");
        }

        JsonNode uso = resposta.path("usage");
        return criarRespostaReal(
                solicitacao,
                conteudo,
                uso.path("prompt_tokens").asInt(0),
                uso.path("completion_tokens").asInt(0),
                tempoMs
        );
    }
}
