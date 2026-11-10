package br.com.linknix.controller;

import br.com.linknix.dto.ExecucaoTesteRequestDTO;
import br.com.linknix.dto.ExecucaoTesteResponseDTO;
import br.com.linknix.dto.ExecutarTesteRequestDTO;
import br.com.linknix.dto.ProcessamentoChamadoResponseDTO;
import br.com.linknix.service.ExecucaoTesteService;
import br.com.linknix.service.ProcessamentoChamadoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({
        "/api/admin/execucoes-teste",
        "/api/pesquisa/execucoes-teste"
})
@RequiredArgsConstructor
public class ExecucaoTesteController {
    private final ExecucaoTesteService service;
    private final ProcessamentoChamadoService processamentoChamadoService;

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

    @PostMapping("/{id}/executar")
    @Operation(summary = "Executa a classificação com o mesmo fluxo configurado para os chamados")
    public ResponseEntity<ProcessamentoChamadoResponseDTO> executar(
            @PathVariable Long id,
            @Valid @RequestBody ExecutarTesteRequestDTO request
    ) {
        return ResponseEntity.ok(
                processamentoChamadoService.executarTeste(id, request)
        );
    }
}
