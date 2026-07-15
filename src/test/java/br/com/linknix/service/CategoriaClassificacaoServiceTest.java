package br.com.linknix.service;

import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.CategoriaClassificacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaClassificacaoServiceTest {

    @Mock
    private CategoriaClassificacaoRepository categoriaRepository;

    @InjectMocks
    private CategoriaClassificacaoService categoriaService;

    @Test
    void deveListarSomenteNomesDasCategoriasAtivas() {
        when(categoriaRepository.findAllByAtivaTrueOrderByNomeAsc())
                .thenReturn(List.of(
                        CategoriaClassificacao.builder().nome("DEV").ativa(true).build(),
                        CategoriaClassificacao.builder().nome("SUPORTE").ativa(true).build()
                ));

        assertEquals(
                List.of("DEV", "SUPORTE"),
                categoriaService.listarNomesAtivos()
        );
    }

    @Test
    void deveImpedirClassificacaoQuandoNaoExistemCategoriasAtivas() {
        when(categoriaRepository.findAllByAtivaTrueOrderByNomeAsc())
                .thenReturn(List.of());

        assertThrows(
                RegraNegocioException.class,
                () -> categoriaService.listarNomesAtivos()
        );
    }
}
