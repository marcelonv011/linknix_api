package br.com.linknix.controller;

import br.com.linknix.dto.ChamadoRequestDTO;
import br.com.linknix.dto.ChamadoResponseDTO;
import br.com.linknix.dto.ProcessamentoChamadoResponseDTO;
import br.com.linknix.dto.ProcessarChamadoRequestDTO;
import br.com.linknix.dto.ValidacaoHumanaRequestDTO;
import br.com.linknix.dto.ValidacaoHumanaResponseDTO;
import br.com.linknix.service.ChamadoService;
import br.com.linknix.service.ProcessamentoChamadoService;
import br.com.linknix.service.ValidacaoHumanaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
@RequiredArgsConstructor
public class ChamadoController {

    private final ProcessamentoChamadoService processamentoService;
    private final ChamadoService chamadoService;
    private final ValidacaoHumanaService validacaoHumanaService;

    @PostMapping
    @SecurityRequirements
    @Operation(summary = "Recebe um chamado externo e o deixa aguardando processamento")
    public ResponseEntity<ChamadoResponseDTO> receber(
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody ChamadoRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chamadoService.receber(apiKey, request));
    }

    @PostMapping("/{id}/processar")
    @SecurityRequirements
    @Operation(summary = "Classifica um chamado previamente recebido")
    public ResponseEntity<ProcessamentoChamadoResponseDTO> processar(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable Long id,
            @Valid @RequestBody ProcessarChamadoRequestDTO request
    ) {
        return ResponseEntity.ok(processamentoService.classificarRecebido(
                apiKey,
                id,
                request.getProvedoresIA()
        ));
    }

    @GetMapping
    public ResponseEntity<List<ChamadoResponseDTO>> listar() {
        return ResponseEntity.ok(chamadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(chamadoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/validacao-humana")
    @Operation(summary = "Registra a categoria correta definida por uma pessoa")
    public ResponseEntity<ValidacaoHumanaResponseDTO> validarCategoriaHumana(
            @PathVariable Long id,
            @Valid @RequestBody ValidacaoHumanaRequestDTO request
    ) {
        return ResponseEntity.ok(validacaoHumanaService.validar(id, request));
    }
}
