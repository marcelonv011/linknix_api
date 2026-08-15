package br.com.linknix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesempenhoModeloResponseDTO {

    private Long modeloIAId;
    private String modeloIANome;
    private String provedorCodigo;
    private Integer totalAvaliacoes;
    private Integer totalAcertos;
    private BigDecimal taxaAcerto;
}

