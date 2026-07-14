package br.com.linknix.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResultadoComparativoResponseDTO {
    private Long id;
    private Long chamadoId;
    private Long categoriaFinalId;
    private String categoriaFinalNome;
    private Long criterioUtilizadoId;
    private String criterioUtilizadoNome;
    private String criterioUtilizadoCodigo;
    private Integer totalModelos;
    private Integer quantidadeConcordante;
    private BigDecimal percentualConcordancia;
    private LocalDateTime criadoEm;
}
