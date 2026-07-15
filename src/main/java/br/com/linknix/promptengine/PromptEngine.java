package br.com.linknix.promptengine;

import br.com.linknix.exception.PromptInvalidoException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PromptEngine {

    private static final Pattern PADRAO_VARIAVEL = Pattern.compile(
            "\\{\\{\\s*([a-zA-Z0-9_]+)\\s*}}"
    );

    private static final Set<String> VARIAVEIS_SUPORTADAS = Set.of(
            "titulo",
            "descricao",
            "categorias",
            "sistema_origem"
    );

    public String montar(String template, ContextoPrompt contexto) {
        validarTemplate(template);

        if (contexto == null) {
            throw new PromptInvalidoException("O contexto do prompt não pode ser nulo.");
        }

        Matcher identificador = PADRAO_VARIAVEL.matcher(template);
        Set<String> variaveisEncontradas = new LinkedHashSet<>();
        List<String> variaveisDesconhecidas = new ArrayList<>();

        while (identificador.find()) {
            String nome = identificador.group(1);
            variaveisEncontradas.add(nome);

            if (!VARIAVEIS_SUPORTADAS.contains(nome)) {
                variaveisDesconhecidas.add(nome);
            }
        }

        if (!variaveisDesconhecidas.isEmpty()) {
            throw new PromptInvalidoException(
                    "O prompt contém variáveis não suportadas: "
                            + String.join(", ", new LinkedHashSet<>(variaveisDesconhecidas))
            );
        }

        validarValoresUtilizados(variaveisEncontradas, contexto);

        Matcher substituidor = PADRAO_VARIAVEL.matcher(template);
        StringBuffer resultado = new StringBuffer();

        while (substituidor.find()) {
            String valor = obterValor(substituidor.group(1), contexto);
            substituidor.appendReplacement(resultado, Matcher.quoteReplacement(valor));
        }

        substituidor.appendTail(resultado);
        validarSintaxeRestante(resultado.toString());

        return resultado.toString();
    }

    private void validarTemplate(String template) {
        if (template == null || template.isBlank()) {
            throw new PromptInvalidoException("O conteúdo do prompt não pode estar vazio.");
        }
    }

    private void validarValoresUtilizados(
            Set<String> variaveisEncontradas,
            ContextoPrompt contexto
    ) {
        for (String variavel : variaveisEncontradas) {
            String valor = obterValor(variavel, contexto);

            if (valor.isBlank()) {
                throw new PromptInvalidoException(
                        "Não existe valor válido para a variável {{" + variavel + "}}."
                );
            }
        }
    }

    private String obterValor(String variavel, ContextoPrompt contexto) {
        return switch (variavel) {
            case "titulo" -> normalizarTexto(contexto.getTitulo());
            case "descricao" -> normalizarTexto(contexto.getDescricao());
            case "categorias" -> normalizarCategorias(contexto.getCategorias());
            case "sistema_origem" -> normalizarTexto(contexto.getSistemaOrigem());
            default -> throw new PromptInvalidoException(
                    "Variável de prompt não suportada: {{" + variavel + "}}."
            );
        };
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String normalizarCategorias(List<String> categorias) {
        if (categorias == null) {
            return "";
        }

        return categorias.stream()
                .filter(categoria -> categoria != null && !categoria.isBlank())
                .map(String::trim)
                .distinct()
                .reduce((primeira, segunda) -> primeira + ", " + segunda)
                .orElse("");
    }

    private void validarSintaxeRestante(String resultado) {
        if (resultado.contains("{{") || resultado.contains("}}")) {
            throw new PromptInvalidoException(
                    "O prompt contém uma variável com sintaxe inválida ou não substituída."
            );
        }
    }
}
