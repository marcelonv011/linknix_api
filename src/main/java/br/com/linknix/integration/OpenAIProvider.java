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
public class OpenAIProvider extends AbstractLLMProvider {

    public OpenAIProvider() {
        super("OPENAI", 120L);
    }

    @Autowired
    public OpenAIProvider(
            ObjectMapper objectMapper,
            @Value("${linknix.ia.modo:simulado}") String modo,
            @Value("${linknix.ia.openai.api-key:}") String apiKey,
            @Value("${linknix.ia.openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${linknix.ia.timeout-segundos:60}") long timeoutSegundos
    ) {
        super(
                "OPENAI",
                120L,
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
        corpo.put("input", solicitacao.getPromptFinal());
        corpo.put("max_output_tokens", 1000);

        ObjectNode formato = corpo.putObject("text")
                .putObject("format");
        formato.put("type", "json_schema");
        formato.put("name", "classificacao_chamado");
        formato.put("strict", true);
        formato.set(
                "schema",
                criarSchemaClassificacao(solicitacao.getCategoriasDisponiveis())
        );

        long inicio = System.nanoTime();
        JsonNode resposta = enviarPost(
                "/responses",
                corpo,
                Map.of("Authorization", "Bearer " + apiKey())
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
        for (JsonNode item : resposta.path("output")) {
            for (JsonNode conteudo : item.path("content")) {
                if ("output_text".equals(conteudo.path("type").asText())
                        && !conteudo.path("text").asText().isBlank()) {
                    return conteudo.path("text").asText();
                }
            }
        }
        throw new IntegracaoException("A OpenAI nao retornou texto na resposta");
    }
}
