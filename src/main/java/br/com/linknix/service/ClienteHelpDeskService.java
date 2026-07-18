package br.com.linknix.service;

import br.com.linknix.dto.ClienteHelpDeskRequestDTO;
import br.com.linknix.dto.ClienteHelpDeskResponseDTO;
import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.entity.Usuario;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ClienteHelpDeskRepository;
import br.com.linknix.security.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteHelpDeskService {

    private static final String MENSAGEM_CLIENTE_NAO_ENCONTRADO =
            "Cliente Help Desk não encontrado ou inativo";

    private final ClienteHelpDeskRepository clienteHelpDeskRepository;
    private final UsuarioService usuarioService;
    private final ApiKeyService apiKeyService;

    @Transactional
    public ClienteHelpDeskResponseDTO criar(ClienteHelpDeskRequestDTO request) {
        String apiKeyHash = apiKeyService.gerarHash(request.getApiKey());
        if (clienteHelpDeskRepository.existsByApiKey(apiKeyHash)) {
            throw new ConflitoException("Esta API Key já está cadastrada");
        }

        Usuario criadoPor = request.getCriadoPorUsuarioId() == null
                ? null
                : usuarioService.buscarEntidadePorId(request.getCriadoPorUsuarioId());
        ClienteHelpDesk cliente = ClienteHelpDesk.builder()
                .nome(request.getNome().trim())
                .sistemaOrigem(request.getSistemaOrigem().trim())
                .apiKey(apiKeyHash)
                .criadoPor(criadoPor)
                .ativo(request.getAtivo() == null || request.getAtivo())
                .build();

        return converterParaResponse(clienteHelpDeskRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteHelpDeskResponseDTO> listarTodos() {
        return clienteHelpDeskRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteHelpDeskResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public ClienteHelpDesk buscarAtivoPorApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RecursoNaoEncontradoException(
                    MENSAGEM_CLIENTE_NAO_ENCONTRADO
            );
        }

        String apiKeyHash = apiKeyService.gerarHash(apiKey);
        return clienteHelpDeskRepository.findByApiKeyAndAtivoTrue(apiKeyHash)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        MENSAGEM_CLIENTE_NAO_ENCONTRADO
                ));
    }

    ClienteHelpDesk buscarEntidadePorId(Long id) {
        return clienteHelpDeskRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente Help Desk não encontrado com o ID " + id
                ));
    }

    private ClienteHelpDeskResponseDTO converterParaResponse(
            ClienteHelpDesk cliente
    ) {
        Usuario criadoPor = cliente.getCriadoPor();
        return ClienteHelpDeskResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .sistemaOrigem(cliente.getSistemaOrigem())
                .apiKeyMascarada("********")
                .criadoPorUsuarioId(criadoPor == null ? null : criadoPor.getId())
                .criadoPorUsuarioNome(criadoPor == null ? null : criadoPor.getNome())
                .ativo(cliente.getAtivo())
                .criadoEm(cliente.getCriadoEm())
                .build();
    }
}
