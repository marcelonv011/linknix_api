package br.com.linknix.service;

import br.com.linknix.dto.ResultadoComparativoResponseDTO;
import br.com.linknix.entity.ResultadoComparativo;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ResultadoComparativoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultadoComparativoService {

    private final ResultadoComparativoRepository resultadoComparativoRepository;

    @Transactional(readOnly = true)
    public ResultadoComparativoResponseDTO buscarPorId(Long id) {
        ResultadoComparativo resultado = resultadoComparativoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Resultado comparativo não encontrado com o ID " + id
                ));

        return converterParaResponse(resultado);
    }

    private ResultadoComparativoResponseDTO converterParaResponse(
            ResultadoComparativo resultado
    ) {
        return ResultadoComparativoResponseDTO.builder()
                .id(resultado.getId())
                .chamadoId(resultado.getChamado().getId())
                .categoriaFinalId(resultado.getCategoriaFinal().getId())
                .categoriaFinalNome(resultado.getCategoriaFinal().getNome())
                .criterioUtilizadoId(resultado.getCriterioUtilizado().getId())
                .criterioUtilizadoNome(resultado.getCriterioUtilizado().getNome())
                .criterioUtilizadoCodigo(resultado.getCriterioUtilizado().getCodigo())
                .totalModelos(resultado.getTotalModelos())
                .quantidadeConcordante(resultado.getQuantidadeConcordante())
                .percentualConcordancia(resultado.getPercentualConcordancia())
                .criadoEm(resultado.getCriadoEm())
                .build();
    }
}
