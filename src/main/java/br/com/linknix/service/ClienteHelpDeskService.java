package br.com.linknix.service;

import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ClienteHelpDeskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteHelpDeskService {

    private static final String MENSAGEM_CLIENTE_NAO_ENCONTRADO =
            "Cliente Help Desk não encontrado ou inativo";

    private final ClienteHelpDeskRepository clienteHelpDeskRepository;

    @Transactional(readOnly = true)
    public ClienteHelpDesk buscarAtivoPorApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RecursoNaoEncontradoException(
                    MENSAGEM_CLIENTE_NAO_ENCONTRADO
            );
        }

        return clienteHelpDeskRepository.findByApiKeyAndAtivoTrue(apiKey.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        MENSAGEM_CLIENTE_NAO_ENCONTRADO
                ));
    }
}
