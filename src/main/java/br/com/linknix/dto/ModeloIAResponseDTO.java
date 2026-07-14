package br.com.linknix.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModeloIAResponseDTO {
    private Long id;
    private String nome;
    private Long provedorId;
    private String provedorNome;
    private String provedorCodigo;
    private String identificadorModelo;
    private String descricao;
    private BigDecimal custoEntradaPorMilTokens;
    private BigDecimal custoSaidaPorMilTokens;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
