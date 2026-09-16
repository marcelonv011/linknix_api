package br.com.linknix.repository;

import br.com.linknix.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    Optional<Prompt> findFirstByAtivoTrueOrderByVersaoDesc();

    List<Prompt> findAllByArquivadoFalse();
}
