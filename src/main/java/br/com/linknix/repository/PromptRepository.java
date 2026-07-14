package br.com.linknix.repository;

import br.com.linknix.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
