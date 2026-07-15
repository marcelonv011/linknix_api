package br.com.linknix.service;

import br.com.linknix.dto.ClassificacaoIAResponseDTO;
import br.com.linknix.dto.PromptResponseDTO;
import br.com.linknix.dto.ResultadoComparativoResponseDTO;
import br.com.linknix.dto.UsuarioResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ClassificacaoIA;
import br.com.linknix.entity.CriterioComparacao;
import br.com.linknix.entity.MetricaClassificacao;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.Prompt;
import br.com.linknix.entity.ProvedorIA;
import br.com.linknix.entity.ResultadoComparativo;
import br.com.linknix.entity.Usuario;
import br.com.linknix.enums.PerfilUsuario;
import br.com.linknix.repository.ClassificacaoIARepository;
import br.com.linknix.repository.MetricaClassificacaoRepository;
import br.com.linknix.repository.PromptRepository;
import br.com.linknix.repository.ResultadoComparativoRepository;
import br.com.linknix.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServicosConsultaTest {

    @Test
    void usuarioServiceDeveConverterEntidadeEmDtoSemSenha() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        UsuarioService service = new UsuarioService(repository);
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Ana")
                .email("ana@linknix.com")
                .senhaHash("hash-que-nao-deve-sair")
                .perfil(PerfilUsuario.ADMINISTRADOR)
                .ativo(true)
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO resposta = service.buscarPorId(1L);

        assertEquals("Ana", resposta.getNome());
        assertEquals("ana@linknix.com", resposta.getEmail());
        assertEquals(PerfilUsuario.ADMINISTRADOR, resposta.getPerfil());
    }

    @Test
    void promptServiceDeveBuscarPromptAtivo() {
        PromptRepository repository = mock(PromptRepository.class);
        PromptService service = new PromptService(repository);
        Prompt prompt = Prompt.builder()
                .id(2L)
                .nome("Classificação")
                .conteudo("Classifique: {{titulo}}")
                .versao(3)
                .ativo(true)
                .autor("Administrador")
                .build();
        when(repository.findFirstByAtivoTrueOrderByVersaoDesc())
                .thenReturn(Optional.of(prompt));

        PromptResponseDTO resposta = service.buscarAtivo();

        assertEquals(2L, resposta.getId());
        assertEquals(3, resposta.getVersao());
        assertTrue(resposta.getAtivo());
    }

    @Test
    void classificacaoServiceDeveMontarDtoComModeloCategoriaEMetrica() {
        ClassificacaoIARepository classificacaoRepository =
                mock(ClassificacaoIARepository.class);
        MetricaClassificacaoRepository metricaRepository =
                mock(MetricaClassificacaoRepository.class);
        ClassificacaoIAService service = new ClassificacaoIAService(
                classificacaoRepository,
                metricaRepository
        );
        ProvedorIA provedor = ProvedorIA.builder().codigo("DEEPSEEK").build();
        ModeloIA modelo = ModeloIA.builder()
                .id(3L)
                .nome("DeepSeek simulado")
                .provedor(provedor)
                .build();
        Prompt prompt = Prompt.builder()
                .id(4L)
                .nome("Prompt principal")
                .versao(1)
                .build();
        CategoriaClassificacao categoria = CategoriaClassificacao.builder()
                .id(5L)
                .nome("SUPORTE")
                .build();
        ClassificacaoIA classificacao = ClassificacaoIA.builder()
                .id(6L)
                .chamado(Chamado.builder().id(7L).build())
                .modeloIA(modelo)
                .prompt(prompt)
                .categoriaAtribuida(categoria)
                .promptFinal("Prompt montado")
                .sucesso(true)
                .build();
        MetricaClassificacao metrica = MetricaClassificacao.builder()
                .id(8L)
                .classificacaoIA(classificacao)
                .acertou(true)
                .build();
        when(classificacaoRepository.findById(6L))
                .thenReturn(Optional.of(classificacao));
        when(metricaRepository.findByClassificacaoIAId(6L))
                .thenReturn(Optional.of(metrica));

        ClassificacaoIAResponseDTO resposta = service.buscarPorId(6L);

        assertEquals("DEEPSEEK", resposta.getProvedorCodigo());
        assertEquals("SUPORTE", resposta.getCategoriaAtribuidaNome());
        assertEquals(8L, resposta.getMetricaClassificacaoId());
        assertTrue(resposta.getAcertou());
    }

    @Test
    void resultadoServiceDeveConverterResultadoComparativoCompleto() {
        ResultadoComparativoRepository repository =
                mock(ResultadoComparativoRepository.class);
        ResultadoComparativoService service =
                new ResultadoComparativoService(repository);
        ResultadoComparativo resultado = ResultadoComparativo.builder()
                .id(9L)
                .chamado(Chamado.builder().id(10L).build())
                .categoriaFinal(CategoriaClassificacao.builder()
                        .id(11L)
                        .nome("DEV")
                        .build())
                .criterioUtilizado(CriterioComparacao.builder()
                        .id(12L)
                        .nome("Maioria")
                        .codigo("MAIORIA")
                        .build())
                .totalModelos(3)
                .quantidadeConcordante(2)
                .percentualConcordancia(new BigDecimal("66.67"))
                .build();
        when(repository.findById(9L)).thenReturn(Optional.of(resultado));

        ResultadoComparativoResponseDTO resposta = service.buscarPorId(9L);

        assertEquals("DEV", resposta.getCategoriaFinalNome());
        assertEquals("MAIORIA", resposta.getCriterioUtilizadoCodigo());
        assertEquals(new BigDecimal("66.67"), resposta.getPercentualConcordancia());
    }
}
