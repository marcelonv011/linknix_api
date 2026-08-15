package br.com.linknix.controller;

import br.com.linknix.dto.DesempenhoModeloResponseDTO;
import br.com.linknix.dto.MetricaClassificacaoResponseDTO;
import br.com.linknix.service.MetricaClassificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metricas")
@RequiredArgsConstructor
public class MetricaClassificacaoController {
    private final MetricaClassificacaoService service;

    @GetMapping
    public ResponseEntity<List<MetricaClassificacaoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/desempenho-modelos")
    public ResponseEntity<List<DesempenhoModeloResponseDTO>> desempenhoModelos() {
        return ResponseEntity.ok(service.calcularDesempenhoPorModelo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricaClassificacaoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
