package br.com.linknix.controller;

import br.com.linknix.dto.PromptRequestDTO;
import br.com.linknix.dto.PromptResponseDTO;
import br.com.linknix.service.PromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/prompts")
@RequiredArgsConstructor
public class PromptController {
    private final PromptService service;

    @PostMapping
    public ResponseEntity<PromptResponseDTO> criar(@Valid @RequestBody PromptRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<PromptResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromptResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<PromptResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }
}
