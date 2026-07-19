package br.com.linknix.controller;

import br.com.linknix.dto.ModeloIARequestDTO;
import br.com.linknix.dto.ModeloIAResponseDTO;
import br.com.linknix.dto.ModeloIAStatusRequestDTO;
import br.com.linknix.service.ModeloIAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/modelos")
@RequiredArgsConstructor
public class ModeloIAController {
    private final ModeloIAService service;

    @PostMapping
    public ResponseEntity<ModeloIAResponseDTO> criar(@Valid @RequestBody ModeloIARequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<ModeloIAResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeloIAResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/ativo")
    public ResponseEntity<ModeloIAResponseDTO> atualizarAtivo(
            @PathVariable Long id,
            @Valid @RequestBody ModeloIAStatusRequestDTO request
    ) {
        return ResponseEntity.ok(service.atualizarAtivo(id, request.getAtivo()));
    }
}
