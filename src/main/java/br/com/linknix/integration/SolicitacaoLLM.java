package br.com.linknix.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SolicitacaoLLM {

    private final String identificadorModelo;
    private final String promptFinal;
    private final List<String> categoriasDisponiveis;
}
