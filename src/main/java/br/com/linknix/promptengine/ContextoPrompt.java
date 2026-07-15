package br.com.linknix.promptengine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ContextoPrompt {

    private String titulo;
    private String descricao;
    private List<String> categorias;
    private String sistemaOrigem;
}
