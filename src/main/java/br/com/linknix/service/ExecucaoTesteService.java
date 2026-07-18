package br.com.linknix.service;

import br.com.linknix.dto.ExecucaoTesteRequestDTO;
import br.com.linknix.dto.ExecucaoTesteResponseDTO;
import br.com.linknix.entity.ExecucaoTeste;
import br.com.linknix.enums.StatusExecucaoTeste;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ExecucaoTesteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecucaoTesteService {

    private final ExecucaoTesteRepository execucaoTesteRepository;

    @Transactional
    public ExecucaoTesteResponseDTO criar(ExecucaoTesteRequestDTO request) {
        ExecucaoTeste execucao = ExecucaoTeste.builder()
                .nome(request.getNome().trim())
                .descricao(request.getDescricao())
                .status(StatusExecucaoTeste.PENDENTE)
                .build();
        return converterParaResponse(execucaoTesteRepository.save(execucao));
    }

    @Transactional(readOnly = true)
    public List<ExecucaoTesteResponseDTO> listarTodos() {
        return execucaoTesteRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExecucaoTesteResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    ExecucaoTeste buscarEntidadePorId(Long id) {
        return execucaoTesteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Execução de teste não encontrada com o ID " + id
                ));
    }

    private ExecucaoTesteResponseDTO converterParaResponse(ExecucaoTeste execucao) {
        return ExecucaoTesteResponseDTO.builder()
                .id(execucao.getId())
                .nome(execucao.getNome())
                .descricao(execucao.getDescricao())
                .status(execucao.getStatus())
                .criadoEm(execucao.getCriadoEm())
                .atualizadoEm(execucao.getAtualizadoEm())
                .build();
    }
}
