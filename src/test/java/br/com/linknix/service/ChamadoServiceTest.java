package br.com.linknix.service;

import br.com.linknix.dto.ChamadoRequestDTO;
import br.com.linknix.dto.ChamadoResponseDTO;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.enums.StatusChamado;
import br.com.linknix.repository.ChamadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        when(chamadoRepository.findByClienteHelpDeskIdAndCodigoExterno(
                10L,
                "TICKET-123"
        )).thenReturn(Optional.empty());
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
    void deveRetornarChamadoExistenteParaRegistroRepetido() {
        when(clienteHelpDeskService.buscarAtivoPorApiKey("chave-do-cliente"))
                .thenReturn(clienteHelpDesk);
        Chamado existente = Chamado.builder()
                .id(7L)
                .codigoExterno("TICKET-123")
                .titulo("Falha no acesso")
                .descricao("Usuario nao consegue entrar")
                .sistemaOrigem("JEDi Educa")
                .status(StatusChamado.RECEBIDO)
                .clienteHelpDesk(clienteHelpDesk)
                .build();
        when(chamadoRepository.findByClienteHelpDeskIdAndCodigoExterno(
                10L,
                "TICKET-123"
        )).thenReturn(Optional.of(existente));

        ChamadoResponseDTO resposta = chamadoService.receber(
                "chave-do-cliente",
                chamadoRequest
        );

        assertEquals(7L, resposta.getId());
        assertEquals(StatusChamado.RECEBIDO, resposta.getStatus());
        verify(chamadoRepository, never()).save(any(Chamado.class));
    }
}
