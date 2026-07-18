package br.com.linknix.controller;

import br.com.linknix.dto.CategoriaClassificacaoRequestDTO;
import br.com.linknix.dto.CategoriaClassificacaoResponseDTO;
import br.com.linknix.service.CategoriaClassificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
public class CategoriaClassificacaoController {
    private final CategoriaClassificacaoService service;

    @PostMapping
    public ResponseEntity<CategoriaClassificacaoResponseDTO> criar(@Valid @RequestBody CategoriaClassificacaoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaClassificacaoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaClassificacaoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
