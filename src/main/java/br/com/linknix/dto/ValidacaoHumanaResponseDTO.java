package br.com.linknix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacaoHumanaResponseDTO {

    private ChamadoResponseDTO chamado;
    private List<ClassificacaoIAResponseDTO> classificacoes;
    private Integer totalClassificacoesAvaliadas;
    private Integer totalAcertos;
}

