package br.com.linknix.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModeloIARequestDTO {
    @NotBlank @Size(max = 150)
    private String nome;
    @NotNull
    private Long provedorId;
    @NotBlank @Size(max = 150)
    private String identificadorModelo;
    @Size(max = 500)
    private String descricao;
    @DecimalMin("0.0")
    private BigDecimal custoEntradaPorMilTokens;
    @DecimalMin("0.0")
    private BigDecimal custoSaidaPorMilTokens;
    private Boolean ativo;
}
