package br.com.linknix.repository;

import br.com.linknix.entity.ResultadoComparativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultadoComparativoRepository extends JpaRepository<ResultadoComparativo, Long> {

    Optional<ResultadoComparativo> findByChamadoId(Long chamadoId);
}
