package br.com.linknix.repository;

import br.com.linknix.entity.ClienteHelpDesk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteHelpDeskRepository extends JpaRepository<ClienteHelpDesk, Long> {

    Optional<ClienteHelpDesk> findByApiKeyAndAtivoTrue(String apiKey);
}
