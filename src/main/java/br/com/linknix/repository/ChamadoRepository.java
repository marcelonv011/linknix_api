package br.com.linknix.repository;

import br.com.linknix.entity.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    Optional<Chamado> findByClienteHelpDeskIdAndCodigoExterno(
            Long clienteHelpDeskId,
            String codigoExterno
    );

    Optional<Chamado> findByIdAndClienteHelpDeskId(
            Long id,
            Long clienteHelpDeskId
    );
}
