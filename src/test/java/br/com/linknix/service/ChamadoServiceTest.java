package br.com.linknix.service;

import br.com.linknix.dto.ChamadoRequestDTO;
import br.com.linknix.dto.ChamadoResponseDTO;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.enums.StatusChamado;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.repository.ChamadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private ClienteHelpDeskService clienteHelpDeskService;

    @InjectMocks
    private ChamadoService chamadoService;

    private ClienteHelpDesk clienteHelpDesk;
    private ChamadoRequestDTO chamadoRequest;

    @BeforeEach
    void configurar() {
        clienteHelpDesk = ClienteHelpDesk.builder()
                .id(10L)
                .nome("JEDi Educação")
                .sistemaOrigem("JEDi Educa")
                .ativo(true)
                .build();

        chamadoRequest = ChamadoRequestDTO.builder()
                .codigoExterno(" TICKET-123 ")
                .titulo(" Falha no acesso ")
                .descricao(" Usuário não consegue entrar no sistema ")
                .build();
    }

    @Test
    void deveObterSistemaDeOrigemAutomaticamenteDoCliente() {
        when(clienteHelpDeskService.buscarAtivoPorApiKey("chave-do-cliente"))
                .thenReturn(clienteHelpDesk);
        when(chamadoRepository.existsByClienteHelpDeskIdAndCodigoExterno(
                10L,
                "TICKET-123"
        )).thenReturn(false);
        when(chamadoRepository.save(any(Chamado.class)))
                .thenAnswer(invocacao -> {
                    Chamado chamado = invocacao.getArgument(0);
                    chamado.setId(1L);
                    return chamado;
                });

        ChamadoResponseDTO resposta = chamadoService.receber(
                "chave-do-cliente",
                chamadoRequest
        );

        assertEquals(1L, resposta.getId());
        assertEquals("TICKET-123", resposta.getCodigoExterno());
        assertEquals("JEDi Educa", resposta.getSistemaOrigem());
        assertEquals(StatusChamado.RECEBIDO, resposta.getStatus());
        assertEquals(10L, resposta.getClienteHelpDeskId());
        assertNull(resposta.getCategoriaEsperadaId());
    }

    @Test
    void deveRejeitarCodigoExternoDuplicadoParaMesmoCliente() {
        when(clienteHelpDeskService.buscarAtivoPorApiKey("chave-do-cliente"))
                .thenReturn(clienteHelpDesk);
        when(chamadoRepository.existsByClienteHelpDeskIdAndCodigoExterno(
                10L,
                "TICKET-123"
        )).thenReturn(true);

        assertThrows(
                ConflitoException.class,
                () -> chamadoService.receber("chave-do-cliente", chamadoRequest)
        );

        verify(chamadoRepository, never()).save(any(Chamado.class));
    }
}
