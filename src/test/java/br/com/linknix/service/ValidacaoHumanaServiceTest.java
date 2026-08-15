package br.com.linknix.service;

import br.com.linknix.dto.ChamadoResponseDTO;
import br.com.linknix.dto.ClassificacaoIAResponseDTO;
import br.com.linknix.dto.ValidacaoHumanaRequestDTO;
import br.com.linknix.dto.ValidacaoHumanaResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidacaoHumanaServiceTest {

    @Mock
    private ChamadoService chamadoService;

    @Mock
    private CategoriaClassificacaoService categoriaService;

    @Mock
    private MetricaClassificacaoService metricaService;

    @Mock
    private ClassificacaoIAService classificacaoIAService;

    @InjectMocks
    private ValidacaoHumanaService validacaoHumanaService;

    @Test
    void deveRegistrarCategoriaHumanaEContabilizarAcertos() {
        Chamado chamado = Chamado.builder().id(10L).build();
        CategoriaClassificacao categoria = CategoriaClassificacao.builder()
                .id(2L)
                .nome("SUPORTE")
                .ativa(true)
                .build();
        ValidacaoHumanaRequestDTO request = ValidacaoHumanaRequestDTO.builder()
                .categoriaId(2L)
                .build();
        List<ClassificacaoIAResponseDTO> classificacoes = List.of(
                ClassificacaoIAResponseDTO.builder().id(1L).acertou(true).build(),
                ClassificacaoIAResponseDTO.builder().id(2L).acertou(false).build(),
                ClassificacaoIAResponseDTO.builder().id(3L).acertou(true).build()
        );

        when(chamadoService.buscarEntidadePorId(10L)).thenReturn(chamado);
        when(categoriaService.buscarEntidadePorId(2L)).thenReturn(categoria);
        when(classificacaoIAService.listarPorChamado(10L))
                .thenReturn(classificacoes);
        when(chamadoService.converterParaResponse(chamado))
                .thenReturn(ChamadoResponseDTO.builder()
                        .id(10L)
                        .categoriaEsperadaId(2L)
                        .categoriaEsperadaNome("SUPORTE")
                        .build());

        ValidacaoHumanaResponseDTO response = validacaoHumanaService
                .validar(10L, request);

        verify(chamadoService).definirCategoriaEsperada(chamado, categoria);
        verify(metricaService).avaliarClassificacoesDoChamado(chamado);
        assertEquals(3, response.getTotalClassificacoesAvaliadas());
        assertEquals(2, response.getTotalAcertos());
        assertEquals("SUPORTE", response.getChamado().getCategoriaEsperadaNome());
    }

    @Test
    void deveRejeitarCategoriaHumanaInativa() {
        Chamado chamado = Chamado.builder().id(10L).build();
        CategoriaClassificacao categoria = CategoriaClassificacao.builder()
                .id(2L)
                .nome("SUPORTE")
                .ativa(false)
                .build();
        ValidacaoHumanaRequestDTO request = ValidacaoHumanaRequestDTO.builder()
                .categoriaId(2L)
                .build();

        when(chamadoService.buscarEntidadePorId(10L)).thenReturn(chamado);
        when(categoriaService.buscarEntidadePorId(2L)).thenReturn(categoria);

        assertThrows(
                RegraNegocioException.class,
                () -> validacaoHumanaService.validar(10L, request)
        );

        verify(chamadoService, never())
                .definirCategoriaEsperada(chamado, categoria);
        verify(metricaService, never()).avaliarClassificacoesDoChamado(chamado);
    }
}

