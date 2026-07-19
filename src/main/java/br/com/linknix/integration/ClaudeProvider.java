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
public class ClaudeProvider extends AbstractLLMProvider {

    public ClaudeProvider() {
        super("CLAUDE", 140L);
    }

    @Autowired
    public ClaudeProvider(
            ObjectMapper objectMapper,
            @Value("${linknix.ia.modo:simulado}") String modo,
            @Value("${linknix.ia.claude.api-key:}") String apiKey,
            @Value("${linknix.ia.claude.base-url:https://api.anthropic.com/v1}") String baseUrl,
            @Value("${linknix.ia.timeout-segundos:60}") long timeoutSegundos
    ) {
        super(
                "CLAUDE",
                140L,
                objectMapper,
                modo,
                apiKey,
                baseUrl,
                timeoutSegundos
        );
    }

    @Override
    protected RespostaLLM executarReal(SolicitacaoLLM solicitacao) {
        ObjectNode corpo = objectMapper().createObjectNode();
        corpo.put("model", solicitacao.getIdentificadorModelo());
        corpo.put("max_tokens", 500);
        corpo.putArray("messages")
                .addObject()
                .put("role", "user")
                .put("content", solicitacao.getPromptFinal());

        ObjectNode formato = corpo.putObject("output_config")
                .putObject("format");
        formato.put("type", "json_schema");
        formato.set(
                "schema",
                criarSchemaClassificacao(solicitacao.getCategoriasDisponiveis())
        );

        long inicio = System.nanoTime();
        JsonNode resposta = enviarPost(
                "/messages",
                corpo,
                Map.of(
                        "x-api-key", apiKey(),
                        "anthropic-version", "2023-06-01"
                )
        );
        long tempoMs = (System.nanoTime() - inicio) / 1_000_000;

        String conteudo = extrairTexto(resposta);
        JsonNode uso = resposta.path("usage");
        return criarRespostaReal(
                solicitacao,
                conteudo,
                uso.path("input_tokens").asInt(0),
                uso.path("output_tokens").asInt(0),
                tempoMs
        );
    }

    private String extrairTexto(JsonNode resposta) {
        for (JsonNode conteudo : resposta.path("content")) {
            if ("text".equals(conteudo.path("type").asText())
                    && !conteudo.path("text").asText().isBlank()) {
                return conteudo.path("text").asText();
            }
        }
        throw new IntegracaoException("A Claude nao retornou texto na resposta");
    }
}
