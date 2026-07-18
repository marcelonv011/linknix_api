package br.com.linknix.repository;

import br.com.linknix.entity.CriterioComparacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CriterioComparacaoRepository extends JpaRepository<CriterioComparacao, Long> {

    Optional<CriterioComparacao> findFirstByAtivoTrueOrderByIdAsc();

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCodigoIgnoreCase(String codigo);
}
