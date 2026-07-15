package br.com.linknix.service;

import br.com.linknix.dto.PromptResponseDTO;
import br.com.linknix.entity.Prompt;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;

    @Transactional(readOnly = true)
    public PromptResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public PromptResponseDTO buscarAtivo() {
        return converterParaResponse(buscarEntidadeAtiva());
    }

    Prompt buscarEntidadePorId(Long id) {
        return promptRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Prompt não encontrado com o ID " + id
                ));
    }

    Prompt buscarEntidadeAtiva() {
        return promptRepository.findFirstByAtivoTrueOrderByVersaoDesc()
                .orElseThrow(() -> new RegraNegocioException(
                        "Nenhum prompt está ativo"
                ));
    }

    private PromptResponseDTO converterParaResponse(Prompt prompt) {
        return PromptResponseDTO.builder()
                .id(prompt.getId())
                .nome(prompt.getNome())
                .descricao(prompt.getDescricao())
                .conteudo(prompt.getConteudo())
                .versao(prompt.getVersao())
                .ativo(prompt.getAtivo())
                .autor(prompt.getAutor())
                .criadoEm(prompt.getCriadoEm())
                .atualizadoEm(prompt.getAtualizadoEm())
                .build();
    }
}
