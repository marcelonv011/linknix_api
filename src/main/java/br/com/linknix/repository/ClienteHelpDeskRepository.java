package br.com.linknix.repository;

import br.com.linknix.entity.ClienteHelpDesk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ClienteHelpDeskRepository extends JpaRepository<ClienteHelpDesk, Long> {

    Optional<ClienteHelpDesk> findByApiKeyAndAtivoTrue(String apiKey);

    Optional<ClienteHelpDesk> findFirstByNomeIgnoreCaseAndAtivoTrue(String nome);
    List<ClienteHelpDesk> findAllByArquivadoFalse();

    boolean existsByApiKey(String apiKey);
}
