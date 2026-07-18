package br.com.linknix.controller;

import br.com.linknix.dto.ClienteHelpDeskRequestDTO;
import br.com.linknix.dto.ClienteHelpDeskResponseDTO;
import br.com.linknix.service.ClienteHelpDeskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clientes-helpdesk")
@RequiredArgsConstructor
public class ClienteHelpDeskController {
    private final ClienteHelpDeskService service;

    @PostMapping
    public ResponseEntity<ClienteHelpDeskResponseDTO> criar(@Valid @RequestBody ClienteHelpDeskRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<ClienteHelpDeskResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteHelpDeskResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
