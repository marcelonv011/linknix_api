package br.com.linknix.repository;

import br.com.linknix.entity.ModeloIA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloIARepository extends JpaRepository<ModeloIA, Long> {

    List<ModeloIA> findAllByAtivoTrueOrderByIdAsc();

    boolean existsByProvedorIdAndIdentificadorModeloIgnoreCase(
            Long provedorId,
            String identificadorModelo
    );
}
