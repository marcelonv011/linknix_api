package br.com.linknix.service;

import br.com.linknix.dto.PromptRequestDTO;
import br.com.linknix.dto.PromptResponseDTO;
import br.com.linknix.entity.Prompt;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;

    @Transactional
    public PromptResponseDTO criar(PromptRequestDTO request) {
        int proximaVersao = promptRepository.findAll().stream()
                .map(Prompt::getVersao)
                .max(Integer::compareTo)
                .orElse(0) + 1;
        Prompt prompt = Prompt.builder()
                .nome(request.getNome().trim())
                .descricao(request.getDescricao())
                .conteudo(request.getConteudo())
                .versao(proximaVersao)
                .ativo(false)
                .autor(request.getAutor().trim())
                .build();
        Prompt salvo = promptRepository.save(prompt);
        if (Boolean.TRUE.equals(request.getAtivo())) {
            return ativar(salvo.getId());
        }
        return converterParaResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<PromptResponseDTO> listarTodos() {
        return promptRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional
    public PromptResponseDTO ativar(Long id) {
        Prompt selecionado = buscarEntidadePorId(id);
        List<Prompt> prompts = promptRepository.findAll();
        prompts.forEach(prompt -> prompt.setAtivo(prompt.getId().equals(id)));
        promptRepository.saveAll(prompts);
        selecionado.setAtivo(true);
        return converterParaResponse(selecionado);
    }

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
