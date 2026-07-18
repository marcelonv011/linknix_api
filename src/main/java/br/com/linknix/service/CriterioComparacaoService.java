package br.com.linknix.service;

import br.com.linknix.dto.CriterioComparacaoRequestDTO;
import br.com.linknix.dto.CriterioComparacaoResponseDTO;
import br.com.linknix.entity.CriterioComparacao;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.CriterioComparacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CriterioComparacaoService {

    private final CriterioComparacaoRepository criterioComparacaoRepository;

    @Transactional
    public CriterioComparacaoResponseDTO criar(
            CriterioComparacaoRequestDTO request
    ) {
        if (criterioComparacaoRepository.existsByNomeIgnoreCase(request.getNome())
                || criterioComparacaoRepository.existsByCodigoIgnoreCase(request.getCodigo())) {
            throw new ConflitoException(
                    "Já existe um critério com este nome ou código"
            );
        }
        CriterioComparacao criterio = CriterioComparacao.builder()
                .nome(request.getNome().trim())
                .codigo(request.getCodigo().trim().toUpperCase())
                .descricao(request.getDescricao().trim())
                .ativo(request.getAtivo() == null || request.getAtivo())
                .build();
        return converterParaResponse(criterioComparacaoRepository.save(criterio));
    }

    @Transactional(readOnly = true)
    public List<CriterioComparacaoResponseDTO> listarTodos() {
        return criterioComparacaoRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    CriterioComparacao buscarEntidadeAtiva() {
        return criterioComparacaoRepository.findFirstByAtivoTrueOrderByIdAsc()
                .orElseThrow(() -> new RegraNegocioException(
                        "Nenhum critério de comparação está ativo"
                ));
    }

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
