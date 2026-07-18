package br.com.linknix.repository;

import br.com.linknix.entity.ProvedorIA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvedorIARepository extends JpaRepository<ProvedorIA, Long> {

    Optional<ProvedorIA> findByCodigoIgnoreCaseAndAtivoTrue(String codigo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCodigoIgnoreCase(String codigo);
}
