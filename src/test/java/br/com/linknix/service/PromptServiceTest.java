package br.com.linknix.service;

import br.com.linknix.entity.Prompt;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.PromptRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PromptServiceTest {

    @Test
    void deveArquivarPromptInativoSemApagarHistorico() {
        PromptRepository repository = mock(PromptRepository.class);
        PromptService service = new PromptService(repository);
        Prompt prompt = Prompt.builder().id(5L).ativo(false).arquivado(false).build();
        when(repository.findById(5L)).thenReturn(Optional.of(prompt));

        service.arquivar(5L);

        assertTrue(prompt.getArquivado());
        verify(repository).save(prompt);
        verify(repository, never()).delete(prompt);
    }

    @Test
    void naoDeveArquivarPromptAtivo() {
        PromptRepository repository = mock(PromptRepository.class);
        PromptService service = new PromptService(repository);
        Prompt prompt = Prompt.builder().id(5L).ativo(true).arquivado(false).build();
        when(repository.findById(5L)).thenReturn(Optional.of(prompt));

        assertThrows(RegraNegocioException.class, () -> service.arquivar(5L));

        assertFalse(prompt.getArquivado());
        verify(repository, never()).save(prompt);
    }

    @Test
    void naoDeveMostrarOuReativarPromptArquivado() {
        PromptRepository repository = mock(PromptRepository.class);
        PromptService service = new PromptService(repository);
        Prompt arquivado = Prompt.builder().id(5L).ativo(false).arquivado(true).build();
        when(repository.findById(5L)).thenReturn(Optional.of(arquivado));
        when(repository.findAllByArquivadoFalse()).thenReturn(List.of());

        assertEquals(0, service.listarTodos().size());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.ativar(5L));
    }
}
