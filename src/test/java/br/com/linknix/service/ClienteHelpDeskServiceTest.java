package br.com.linknix.service;

import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ClienteHelpDeskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteHelpDeskServiceTest {

    @Mock
    private ClienteHelpDeskRepository clienteHelpDeskRepository;

    @InjectMocks
    private ClienteHelpDeskService clienteHelpDeskService;

    @Test
    void deveEncontrarClienteAtivoPelaApiKey() {
        ClienteHelpDesk cliente = ClienteHelpDesk.builder()
                .id(10L)
                .nome("JEDi Educação")
                .ativo(true)
                .build();
        when(clienteHelpDeskRepository.findByApiKeyAndAtivoTrue("chave-valida"))
                .thenReturn(Optional.of(cliente));

        ClienteHelpDesk encontrado = clienteHelpDeskService
                .buscarAtivoPorApiKey(" chave-valida ");

        assertEquals(10L, encontrado.getId());
    }

    @Test
    void deveRejeitarApiKeyInvalidaOuDeClienteInativo() {
        when(clienteHelpDeskRepository.findByApiKeyAndAtivoTrue("chave-invalida"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteHelpDeskService.buscarAtivoPorApiKey("chave-invalida")
        );
    }
}
