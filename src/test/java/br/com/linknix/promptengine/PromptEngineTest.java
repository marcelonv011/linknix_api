package br.com.linknix.promptengine;

import br.com.linknix.exception.PromptInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptEngineTest {

    private PromptEngine promptEngine;

    @BeforeEach
    void configurar() {
        promptEngine = new PromptEngine();
    }

    @Test
    void deveSubstituirTodasAsVariaveisSuportadas() {
        String template = "Título: {{titulo}}\nDescrição: {{ descricao }}\n"
                + "Categorias: {{categorias}}\nOrigem: {{sistema_origem}}";

        ContextoPrompt contexto = ContextoPrompt.builder()
                .titulo("Falha no login")
                .descricao("Usuário não consegue acessar o sistema")
                .categorias(List.of("DEV", " SUPORTE ", "DEV"))
                .sistemaOrigem("JEDi Educa")
                .build();

        String resultado = promptEngine.montar(template, contexto);

        assertEquals(
                "Título: Falha no login\n"
                        + "Descrição: Usuário não consegue acessar o sistema\n"
                        + "Categorias: DEV, SUPORTE\n"
                        + "Origem: JEDi Educa",
                resultado
        );
    }

    @Test
    void deveSubstituirValoresComCaracteresEspeciais() {
        ContextoPrompt contexto = ContextoPrompt.builder()
                .titulo("Erro no valor $100")
                .descricao("Caminho C:\\sistema\\arquivo")
                .build();

        String resultado = promptEngine.montar(
                "{{titulo}} - {{descricao}}",
                contexto
        );

        assertEquals(
                "Erro no valor $100 - Caminho C:\\sistema\\arquivo",
                resultado
        );
    }

    @Test
    void deveExigirSomenteValoresUtilizadosPeloTemplate() {
        ContextoPrompt contexto = ContextoPrompt.builder()
                .titulo("Impressora sem papel")
                .build();

        assertEquals(
                "Classifique: Impressora sem papel",
                promptEngine.montar("Classifique: {{titulo}}", contexto)
        );
    }

    @Test
    void deveRejeitarVariavelNaoSuportada() {
        PromptInvalidoException exception = assertThrows(
                PromptInvalidoException.class,
                () -> promptEngine.montar(
                        "Prioridade: {{prioridade}}",
                        ContextoPrompt.builder().build()
                )
        );

        assertTrue(exception.getMessage().contains("prioridade"));
    }

    @Test
    void deveRejeitarValorAusenteParaVariavelUtilizada() {
        assertThrows(
                PromptInvalidoException.class,
                () -> promptEngine.montar(
                        "Categorias: {{categorias}}",
                        ContextoPrompt.builder().categorias(List.of()).build()
                )
        );
    }

    @Test
    void deveRejeitarTemplateVazio() {
        assertThrows(
                PromptInvalidoException.class,
                () -> promptEngine.montar("  ", ContextoPrompt.builder().build())
        );
    }

    @Test
    void deveRejeitarSintaxeIncompleta() {
        assertThrows(
                PromptInvalidoException.class,
                () -> promptEngine.montar(
                        "Título: {{titulo",
                        ContextoPrompt.builder().titulo("Falha").build()
                )
        );
    }
}
