package br.com.linknix.controller;

import br.com.linknix.dto.CategoriaClassificacaoResponseDTO;
import br.com.linknix.dto.ClienteHelpDeskResponseDTO;
import br.com.linknix.dto.CriterioComparacaoResponseDTO;
import br.com.linknix.dto.ModeloIAResponseDTO;
import br.com.linknix.dto.PromptResponseDTO;
import br.com.linknix.dto.ProvedorIAResponseDTO;
import br.com.linknix.service.CategoriaClassificacaoService;
import br.com.linknix.service.ClienteHelpDeskService;
import br.com.linknix.service.CriterioComparacaoService;
import br.com.linknix.service.ModeloIAService;
import br.com.linknix.service.PromptService;
import br.com.linknix.service.ProvedorIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pesquisa")
@RequiredArgsConstructor
public class PesquisaConsultaController {

    private final ClienteHelpDeskService clienteService;
    private final CategoriaClassificacaoService categoriaService;
    private final ModeloIAService modeloService;
    private final PromptService promptService;
    private final ProvedorIAService provedorService;
    private final CriterioComparacaoService criterioService;

    @GetMapping("/clientes-helpdesk")
    public ResponseEntity<List<ClienteHelpDeskResponseDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaClassificacaoResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/modelos")
    public ResponseEntity<List<ModeloIAResponseDTO>> listarModelos() {
        return ResponseEntity.ok(modeloService.listarTodos());
    }

    @GetMapping("/prompts")
    public ResponseEntity<List<PromptResponseDTO>> listarPrompts() {
        return ResponseEntity.ok(promptService.listarTodos());
    }

    @GetMapping("/provedores")
    public ResponseEntity<List<ProvedorIAResponseDTO>> listarProvedores() {
        return ResponseEntity.ok(provedorService.listarTodos());
    }

    @GetMapping("/criterios")
    public ResponseEntity<List<CriterioComparacaoResponseDTO>> listarCriterios() {
        return ResponseEntity.ok(criterioService.listarTodos());
    }
}
