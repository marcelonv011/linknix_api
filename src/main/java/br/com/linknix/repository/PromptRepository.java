package br.com.linknix.repository;

import br.com.linknix.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    Optional<Prompt> findFirstByAtivoTrueOrderByVersaoDesc();
}
