package br.com.linknix.integration;

import br.com.linknix.exception.IntegracaoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractLLMProvider implements LLMProvider {

    private static final int CARACTERES_POR_TOKEN_SIMULADO = 4;

    private final String codigo;
    private final long tempoRespostaMsSimulado;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String modo;
    private final String apiKey;
    private final String baseUrl;
    private final Duration timeout;

    protected AbstractLLMProvider(
            String codigo,
            long tempoRespostaMs
    ) {
        this(
                codigo,
                tempoRespostaMs,
                new ObjectMapper(),
                "simulado",
                "",
                "",
                60
        );
    }

    protected AbstractLLMProvider(
            String codigo,
            long tempoRespostaMs,
            ObjectMapper objectMapper,
            String modo,
            String apiKey,
            String baseUrl,
            long timeoutSegundos
    ) {
        this.codigo = codigo;
        this.tempoRespostaMsSimulado = tempoRespostaMs;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSegundos))
                .build();
        this.modo = modo == null ? "simulado" : modo.trim();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
        this.timeout = Duration.ofSeconds(timeoutSegundos);
    }

    @Override
    public String obterCodigo() {
        return codigo;
    }

    @Override
    public RespostaLLM executar(SolicitacaoLLM solicitacao) {
        validar(solicitacao);

        if ("simulado".equalsIgnoreCase(modo)) {
            return executarSimulado(solicitacao);
        }

        if (!"real".equalsIgnoreCase(modo)) {
            throw new IntegracaoException(
                    "O modo de IA deve ser 'simulado' ou 'real'"
            );
        }

        if (apiKey.isBlank()) {
            throw new IntegracaoException(
                    "A API Key do provedor " + codigo + " nao foi configurada"
            );
        }

        if (baseUrl.isBlank()) {
            throw new IntegracaoException(
                    "A URL do provedor " + codigo + " nao foi configurada"
            );
        }

        return executarReal(solicitacao);
    }

    protected abstract RespostaLLM executarReal(SolicitacaoLLM solicitacao);

    protected ObjectNode criarSchemaClassificacao(List<String> categorias) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");

        ObjectNode propriedades = schema.putObject("properties");
        ObjectNode categoria = propriedades.putObject("categoria");
        categoria.put("type", "string");
        ArrayNode valoresPermitidos = categoria.putArray("enum");
        normalizarCategorias(categorias).forEach(valoresPermitidos::add);

        propriedades.putObject("justificativa")
                .put("type", "string");

        schema.putArray("required")
                .add("categoria")
                .add("justificativa");
        schema.put("additionalProperties", false);
        return schema;
    }

    protected JsonNode enviarPost(
            String caminho,
            JsonNode corpo,
            Map<String, String> cabecalhos
    ) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(criarUri(caminho))
                    .timeout(timeout)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(corpo)
                    ));
            cabecalhos.forEach(requestBuilder::header);

            HttpResponse<String> response = httpClient.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IntegracaoException(
                        "O provedor " + codigo + " respondeu com HTTP "
                                + response.statusCode() + ": "
                                + limitarMensagem(response.body())
                );
            }

            return objectMapper.readTree(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IntegracaoException(
                    "A chamada ao provedor " + codigo + " foi interrompida",
                    exception
            );
        } catch (IOException | IllegalArgumentException exception) {
            throw new IntegracaoException(
                    "Nao foi possivel chamar o provedor " + codigo,
                    exception
            );
        }
    }

    protected RespostaLLM criarRespostaReal(
            SolicitacaoLLM solicitacao,
            String conteudo,
            int tokensEntrada,
            int tokensSaida,
            long tempoRespostaMs
    ) {
        JsonNode classificacao;
        try {
            classificacao = objectMapper.readTree(removerMarcadoresJson(conteudo));
        } catch (JsonProcessingException exception) {
            throw new IntegracaoException(
                    "O provedor " + codigo + " retornou uma classificacao invalida",
                    exception
            );
        }

        String categoriaRecebida = textoObrigatorio(classificacao, "categoria");
        String categoriaNormalizada = normalizarCategoriaRecebida(
                categoriaRecebida,
                solicitacao.getCategoriasDisponiveis()
        );
        return RespostaLLM.builder()
                .codigoProvedor(codigo)
                .identificadorModelo(solicitacao.getIdentificadorModelo().trim())
                .categoriaSugerida(categoriaNormalizada)
                .justificativa(textoObrigatorio(classificacao, "justificativa"))
                .respostaBruta(conteudo)
                .tokensEntrada(Math.max(0, tokensEntrada))
                .tokensSaida(Math.max(0, tokensSaida))
                .tempoRespostaMs(tempoRespostaMs)
                .build();
    }

    protected ObjectMapper objectMapper() {
        return objectMapper;
    }

    protected String apiKey() {
        return apiKey;
    }

    private RespostaLLM executarSimulado(SolicitacaoLLM solicitacao) {
        List<String> categorias = normalizarCategorias(
                solicitacao.getCategoriasDisponiveis()
        );

        int indiceCategoria = Math.floorMod(
                (solicitacao.getPromptFinal() + codigo).hashCode(),
                categorias.size()
        );
        String categoriaSugerida = categorias.get(indiceCategoria);
        String justificativa = "Resposta simulada do provedor " + codigo
                + " para validar o fluxo de classificacao.";
        String respostaBruta = "categoria=" + categoriaSugerida
                + "; justificativa=" + justificativa;

        return RespostaLLM.builder()
                .codigoProvedor(codigo)
                .identificadorModelo(solicitacao.getIdentificadorModelo().trim())
                .categoriaSugerida(categoriaSugerida)
                .justificativa(justificativa)
                .respostaBruta(respostaBruta)
                .tokensEntrada(estimarTokens(solicitacao.getPromptFinal()))
                .tokensSaida(estimarTokens(respostaBruta))
                .tempoRespostaMs(tempoRespostaMsSimulado)
                .build();
    }

    private void validar(SolicitacaoLLM solicitacao) {
        if (solicitacao == null) {
            throw new IntegracaoException("A solicitacao ao provedor nao pode ser nula");
        }

        if (solicitacao.getIdentificadorModelo() == null
                || solicitacao.getIdentificadorModelo().isBlank()) {
            throw new IntegracaoException("O identificador do modelo e obrigatorio");
        }

        if (solicitacao.getPromptFinal() == null
                || solicitacao.getPromptFinal().isBlank()) {
            throw new IntegracaoException("O prompt final e obrigatorio");
        }

        if (solicitacao.getCategoriasDisponiveis() == null) {
            throw new IntegracaoException("As categorias disponiveis sao obrigatorias");
        }

        if (normalizarCategorias(solicitacao.getCategoriasDisponiveis()).isEmpty()) {
            throw new IntegracaoException(
                    "Nenhuma categoria ativa foi informada ao provedor " + codigo
            );
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

    private String normalizarCategoriaRecebida(
            String categoriaRecebida,
            List<String> categoriasPermitidas
    ) {
        return normalizarCategorias(categoriasPermitidas).stream()
                .filter(categoria -> categoria.equalsIgnoreCase(categoriaRecebida.trim()))
                .findFirst()
                .orElseThrow(() -> new IntegracaoException(
                        "O provedor " + codigo + " retornou uma categoria nao permitida: "
                                + categoriaRecebida
                ));
    }

    private String textoObrigatorio(JsonNode node, String campo) {
        JsonNode valor = node.get(campo);
        if (valor == null || !valor.isTextual() || valor.asText().isBlank()) {
            throw new IntegracaoException(
                    "O provedor " + codigo + " nao retornou o campo " + campo
            );
        }
        return valor.asText().trim();
    }

    private int estimarTokens(String texto) {
        return Math.max(
                1,
                (int) Math.ceil((double) texto.length() / CARACTERES_POR_TOKEN_SIMULADO)
        );
    }

    private URI criarUri(String caminho) {
        String urlBase = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        String caminhoNormalizado = caminho.startsWith("/")
                ? caminho
                : "/" + caminho;
        return URI.create(urlBase + caminhoNormalizado);
    }

    private String removerMarcadoresJson(String conteudo) {
        String normalizado = conteudo == null ? "" : conteudo.trim();
        if (normalizado.startsWith("```json")) {
            normalizado = normalizado.substring(7);
        } else if (normalizado.startsWith("```")) {
            normalizado = normalizado.substring(3);
        }
        if (normalizado.endsWith("```")) {
            normalizado = normalizado.substring(0, normalizado.length() - 3);
        }
        return normalizado.trim();
    }

    private String limitarMensagem(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return "sem detalhes";
        }
        String normalizada = mensagem.replaceAll("\\s+", " ").trim();
        return normalizada.substring(0, Math.min(normalizada.length(), 500));
    }
}
