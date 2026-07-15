package br.com.linknix.repository;

import br.com.linknix.entity.MetricaClassificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetricaClassificacaoRepository extends JpaRepository<MetricaClassificacao, Long> {

    Optional<MetricaClassificacao> findByClassificacaoIAId(Long classificacaoIAId);
}
