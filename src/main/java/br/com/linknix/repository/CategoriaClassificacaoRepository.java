package br.com.linknix.repository;

import br.com.linknix.entity.CategoriaClassificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaClassificacaoRepository extends JpaRepository<CategoriaClassificacao, Long> {

    List<CategoriaClassificacao> findAllByAtivaTrueOrderByNomeAsc();

    Optional<CategoriaClassificacao> findByNomeIgnoreCaseAndAtivaTrue(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
