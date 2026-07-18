package br.com.linknix.repository;

import br.com.linknix.entity.ClassificacaoIA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassificacaoIARepository extends JpaRepository<ClassificacaoIA, Long> {

    List<ClassificacaoIA> findAllByChamadoIdOrderByCriadoEmAsc(Long chamadoId);
}
