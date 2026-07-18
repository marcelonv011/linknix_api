package br.com.linknix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessamentoChamadoResponseDTO {

    private ChamadoResponseDTO chamado;
    private List<ClassificacaoIAResponseDTO> classificacoes;
    private ResultadoComparativoResponseDTO resultado;
}
