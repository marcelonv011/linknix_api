package br.com.linknix.repository;

import br.com.linknix.entity.ExecucaoTeste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecucaoTesteRepository extends JpaRepository<ExecucaoTeste, Long> {
}
