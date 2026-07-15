package br.com.linknix.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class RespostaLLM {

    private final String codigoProvedor;
    private final String identificadorModelo;
    private final String categoriaSugerida;
    private final BigDecimal nivelConfianca;
    private final String justificativa;
    private final String respostaBruta;
    private final Integer tokensEntrada;
    private final Integer tokensSaida;
    private final Long tempoRespostaMs;
}
