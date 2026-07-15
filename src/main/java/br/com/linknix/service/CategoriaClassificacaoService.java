package br.com.linknix.service;

import br.com.linknix.entity.CategoriaClassificacao;
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
}
