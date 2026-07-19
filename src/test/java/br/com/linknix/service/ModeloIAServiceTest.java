package br.com.linknix.service;

import br.com.linknix.dto.ModeloIAResponseDTO;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.ProvedorIA;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.ModeloIARepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModeloIAServiceTest {

    @Test
    void deveSelecionarSomenteOsProvedoresInformados() {
        ModeloIARepository repository = mock(ModeloIARepository.class);
        ModeloIAService service = new ModeloIAService(
                repository,
                mock(ProvedorIAService.class)
        );
        when(repository.findAllByAtivoTrueOrderByIdAsc()).thenReturn(List.of(
                criarModelo(1L, "OPENAI", true),
                criarModelo(2L, "CLAUDE", true),
                criarModelo(3L, "DEEPSEEK", true)
        ));

        List<ModeloIA> selecionados = service
                .listarEntidadesAtivasSelecionadas(List.of("openai", "CLAUDE"));

        assertEquals(2, selecionados.size());
        assertEquals("OPENAI", selecionados.get(0).getProvedor().getCodigo());
        assertEquals("CLAUDE", selecionados.get(1).getProvedor().getCodigo());
    }

    @Test
    void deveRejeitarProvedorSemModeloAtivo() {
        ModeloIARepository repository = mock(ModeloIARepository.class);
        ModeloIAService service = new ModeloIAService(
                repository,
                mock(ProvedorIAService.class)
        );
        when(repository.findAllByAtivoTrueOrderByIdAsc()).thenReturn(List.of(
                criarModelo(1L, "OPENAI", true)
        ));

        assertThrows(
                RegraNegocioException.class,
                () -> service.listarEntidadesAtivasSelecionadas(
                        List.of("DEEPSEEK")
                )
        );
    }

    @Test
    void deveDesativarModeloExistente() {
        ModeloIARepository repository = mock(ModeloIARepository.class);
        ModeloIAService service = new ModeloIAService(
                repository,
                mock(ProvedorIAService.class)
        );
        ModeloIA modelo = criarModelo(3L, "DEEPSEEK", true);
        when(repository.findById(3L)).thenReturn(Optional.of(modelo));
        when(repository.save(any(ModeloIA.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        ModeloIAResponseDTO resposta = service.atualizarAtivo(3L, false);

        assertFalse(resposta.getAtivo());
    }

    private ModeloIA criarModelo(Long id, String codigo, boolean ativo) {
        ProvedorIA provedor = ProvedorIA.builder()
                .id(id)
                .nome(codigo)
                .codigo(codigo)
                .ativo(true)
                .build();
        return ModeloIA.builder()
                .id(id)
                .nome(codigo + " modelo")
                .provedor(provedor)
                .identificadorModelo(codigo.toLowerCase() + "-modelo")
                .ativo(ativo)
                .build();
    }
}
