package br.com.linknix.controller;

import br.com.linknix.dto.ProvedorIARequestDTO;
import br.com.linknix.dto.ProvedorIAResponseDTO;
import br.com.linknix.service.ProvedorIAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/provedores")
@RequiredArgsConstructor
public class ProvedorIAController {
    private final ProvedorIAService service;

    @PostMapping
    public ResponseEntity<ProvedorIAResponseDTO> criar(@Valid @RequestBody ProvedorIARequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<ProvedorIAResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvedorIAResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
