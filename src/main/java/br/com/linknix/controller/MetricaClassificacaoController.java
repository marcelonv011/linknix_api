package br.com.linknix.controller;

import br.com.linknix.dto.MetricaClassificacaoResponseDTO;
import br.com.linknix.service.MetricaClassificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metricas")
@RequiredArgsConstructor
public class MetricaClassificacaoController {
    private final MetricaClassificacaoService service;

    @GetMapping("/{id}")
    public ResponseEntity<MetricaClassificacaoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
