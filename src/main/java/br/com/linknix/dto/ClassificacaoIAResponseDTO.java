package br.com.linknix.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassificacaoIAResponseDTO {
    private Long id;
    private Long chamadoId;
    private Long modeloIAId;
    private String modeloIANome;
    private String provedorCodigo;
    private Long promptId;
    private String promptNome;
    private Integer promptVersao;
    private Long categoriaAtribuidaId;
    private String categoriaAtribuidaNome;
    private Long execucaoTesteId;
    private String justificativa;
    private String promptFinal;
    private String respostaBruta;
    private Integer tokensEntrada;
    private Integer tokensSaida;
    private Long tempoRespostaMs;
    private BigDecimal custoEstimado;
    private Boolean sucesso;
    private String mensagemErro;
    private Long metricaClassificacaoId;
    private Boolean acertou;
    private LocalDateTime criadoEm;
}
