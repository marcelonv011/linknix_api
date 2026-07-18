package br.com.linknix.service;

import br.com.linknix.dto.CategoriaClassificacaoRequestDTO;
import br.com.linknix.dto.CategoriaClassificacaoResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.CategoriaClassificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaClassificacaoService {

    private final CategoriaClassificacaoRepository categoriaClassificacaoRepository;

    @Transactional
    public CategoriaClassificacaoResponseDTO criar(
            CategoriaClassificacaoRequestDTO request
    ) {
        if (categoriaClassificacaoRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflitoException("Já existe uma categoria com este nome");
        }
        CategoriaClassificacao categoria = CategoriaClassificacao.builder()
                .nome(request.getNome().trim().toUpperCase())
                .descricao(request.getDescricao().trim())
                .ativa(request.getAtiva() == null || request.getAtiva())
                .build();
        return converterParaResponse(
                categoriaClassificacaoRepository.save(categoria)
        );
    }

    @Transactional(readOnly = true)
    public List<CategoriaClassificacaoResponseDTO> listarTodas() {
        return categoriaClassificacaoRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaClassificacaoResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<String> listarNomesAtivos() {
        List<String> categorias = categoriaClassificacaoRepository
                .findAllByAtivaTrueOrderByNomeAsc()
                .stream()
                .map(CategoriaClassificacao::getNome)
                .toList();

        if (categorias.isEmpty()) {
            throw new RegraNegocioException(
                    "Nenhuma categoria de classificação está ativa"
            );
        }

        return categorias;
    }

    List<CategoriaClassificacao> listarEntidadesAtivas() {
        List<CategoriaClassificacao> categorias = categoriaClassificacaoRepository
                .findAllByAtivaTrueOrderByNomeAsc();
        if (categorias.isEmpty()) {
            throw new RegraNegocioException(
                    "Nenhuma categoria de classificação está ativa"
            );
        }
        return categorias;
    }

    CategoriaClassificacao buscarAtivaPorNome(String nome) {
        return categoriaClassificacaoRepository
                .findByNomeIgnoreCaseAndAtivaTrue(nome)
                .orElseThrow(() -> new RegraNegocioException(
                        "A IA retornou uma categoria inválida: " + nome
                ));
    }

    CategoriaClassificacao buscarEntidadePorId(Long id) {
        return categoriaClassificacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria não encontrada com o ID " + id
                ));
    }

    private CategoriaClassificacaoResponseDTO converterParaResponse(
            CategoriaClassificacao categoria
    ) {
        return CategoriaClassificacaoResponseDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativa(categoria.getAtiva())
                .criadaEm(categoria.getCriadaEm())
                .build();
    }
}
