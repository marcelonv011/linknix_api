package br.com.linknix.service;

import br.com.linknix.dto.CriterioComparacaoResponseDTO;
import br.com.linknix.entity.CriterioComparacao;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.CriterioComparacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriterioComparacaoService {

    private final CriterioComparacaoRepository criterioComparacaoRepository;

    @Transactional(readOnly = true)
    public CriterioComparacaoResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    CriterioComparacao buscarEntidadePorId(Long id) {
        return criterioComparacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Critério de comparação não encontrado com o ID " + id
                ));
    }

    private CriterioComparacaoResponseDTO converterParaResponse(
            CriterioComparacao criterio
    ) {
        return CriterioComparacaoResponseDTO.builder()
                .id(criterio.getId())
                .nome(criterio.getNome())
                .codigo(criterio.getCodigo())
                .descricao(criterio.getDescricao())
                .ativo(criterio.getAtivo())
                .criadoEm(criterio.getCriadoEm())
                .atualizadoEm(criterio.getAtualizadoEm())
                .build();
    }
}
