package br.com.linknix.controller;

import br.com.linknix.dto.ResultadoComparativoResponseDTO;
import br.com.linknix.service.ResultadoComparativoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resultados")
@RequiredArgsConstructor
public class ResultadoComparativoController {

    private final ResultadoComparativoService resultadoService;

    @GetMapping("/{id}")
    public ResponseEntity<ResultadoComparativoResponseDTO> buscar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(resultadoService.buscarPorId(id));
    }

    @GetMapping("/chamado/{chamadoId}")
    public ResponseEntity<ResultadoComparativoResponseDTO> buscarPorChamado(
            @PathVariable Long chamadoId
    ) {
        return ResponseEntity.ok(resultadoService.buscarPorChamado(chamadoId));
    }
}
