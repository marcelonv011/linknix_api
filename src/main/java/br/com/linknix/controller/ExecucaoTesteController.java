package br.com.linknix.controller;

import br.com.linknix.dto.ExecucaoTesteRequestDTO;
import br.com.linknix.dto.ExecucaoTesteResponseDTO;
import br.com.linknix.service.ExecucaoTesteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/execucoes-teste")
@RequiredArgsConstructor
public class ExecucaoTesteController {
    private final ExecucaoTesteService service;

    @PostMapping
    public ResponseEntity<ExecucaoTesteResponseDTO> criar(@Valid @RequestBody ExecucaoTesteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<ExecucaoTesteResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExecucaoTesteResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
