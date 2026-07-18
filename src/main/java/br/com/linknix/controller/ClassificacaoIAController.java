package br.com.linknix.controller;

import br.com.linknix.dto.ClassificacaoIAResponseDTO;
import br.com.linknix.service.ClassificacaoIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classificacoes")
@RequiredArgsConstructor
public class ClassificacaoIAController {

    private final ClassificacaoIAService classificacaoIAService;

    @GetMapping("/{id}")
    public ResponseEntity<ClassificacaoIAResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(classificacaoIAService.buscarPorId(id));
    }

    @GetMapping("/chamado/{chamadoId}")
    public ResponseEntity<List<ClassificacaoIAResponseDTO>> listarPorChamado(
            @PathVariable Long chamadoId
    ) {
        return ResponseEntity.ok(
                classificacaoIAService.listarPorChamado(chamadoId)
        );
    }
}
