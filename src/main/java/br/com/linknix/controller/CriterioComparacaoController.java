package br.com.linknix.controller;

import br.com.linknix.dto.CriterioComparacaoRequestDTO;
import br.com.linknix.dto.CriterioComparacaoResponseDTO;
import br.com.linknix.service.CriterioComparacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/criterios")
@RequiredArgsConstructor
public class CriterioComparacaoController {
    private final CriterioComparacaoService service;

    @PostMapping
    public ResponseEntity<CriterioComparacaoResponseDTO> criar(@Valid @RequestBody CriterioComparacaoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<CriterioComparacaoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterioComparacaoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
