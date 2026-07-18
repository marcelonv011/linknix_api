package br.com.linknix.service;

import br.com.linknix.dto.ChamadoRequestDTO;
import br.com.linknix.dto.ProcessamentoChamadoResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.entity.CriterioComparacao;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.Prompt;
import br.com.linknix.entity.ProvedorIA;
import br.com.linknix.enums.StatusChamado;
import br.com.linknix.repository.CategoriaClassificacaoRepository;
import br.com.linknix.repository.ClienteHelpDeskRepository;
import br.com.linknix.repository.CriterioComparacaoRepository;
import br.com.linknix.repository.ModeloIARepository;
import br.com.linknix.repository.PromptRepository;
import br.com.linknix.repository.ProvedorIARepository;
import br.com.linknix.security.ApiKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ProcessamentoChamadoServiceTest {

    @Autowired
    private ProcessamentoChamadoService processamentoService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private ClienteHelpDeskRepository clienteRepository;

    @Autowired
    private CategoriaClassificacaoRepository categoriaRepository;

    @Autowired
    private ProvedorIARepository provedorRepository;

    @Autowired
    private ModeloIARepository modeloRepository;

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private CriterioComparacaoRepository criterioRepository;

    @Test
    void deveReceberClassificarCompararEFinalizarChamado() {
        String apiKey = "api-key-segura-para-o-teste-integrado";
        clienteRepository.save(ClienteHelpDesk.builder()
                .nome("JEDi")
                .sistemaOrigem("JEDi Educa")
                .apiKey(apiKeyService.gerarHash(apiKey))
                .ativo(true)
                .build());
        categoriaRepository.saveAll(List.of(
                CategoriaClassificacao.builder()
                        .nome("DEV")
                        .descricao("Desenvolvimento")
                        .ativa(true)
                        .build(),
                CategoriaClassificacao.builder()
                        .nome("SUPORTE")
                        .descricao("Suporte técnico")
                        .ativa(true)
                        .build()
        ));
        promptRepository.save(Prompt.builder()
                .nome("Prompt ativo")
                .descricao("Prompt de teste")
                .conteudo("Título: {{titulo}}\nDescrição: {{descricao}}\n"
                        + "Categorias: {{categorias}}\nOrigem: {{sistema_origem}}")
                .versao(1)
                .ativo(true)
                .autor("Teste")
                .build());
        criterioRepository.save(CriterioComparacao.builder()
                .nome("Maioria")
                .codigo("MAIORIA")
                .descricao("Maioria simples")
                .ativo(true)
                .build());

        criarModelo("OpenAI", "OPENAI", "openai-teste");
        criarModelo("Claude", "CLAUDE", "claude-teste");
        criarModelo("DeepSeek", "DEEPSEEK", "deepseek-teste");

        ProcessamentoChamadoResponseDTO resposta = processamentoService
                .receberEClassificar(apiKey, ChamadoRequestDTO.builder()
                        .codigoExterno("TICKET-100")
                        .titulo("Falha ao acessar o sistema")
                        .descricao("Usuário recebe erro ao fazer login")
                        .build());

        assertEquals(StatusChamado.CLASSIFICADO, resposta.getChamado().getStatus());
        assertEquals("JEDi Educa", resposta.getChamado().getSistemaOrigem());
        assertEquals(3, resposta.getClassificacoes().size());
        assertEquals(3, resposta.getResultado().getTotalModelos());
        assertNotNull(resposta.getResultado().getCategoriaFinalNome());
    }

    private void criarModelo(
            String nomeProvedor,
            String codigoProvedor,
            String identificadorModelo
    ) {
        ProvedorIA provedor = provedorRepository.save(ProvedorIA.builder()
                .nome(nomeProvedor)
                .codigo(codigoProvedor)
                .descricao("Provider simulado")
                .ativo(true)
                .build());
        modeloRepository.save(ModeloIA.builder()
                .nome(nomeProvedor + " simulado")
                .provedor(provedor)
                .identificadorModelo(identificadorModelo)
                .custoEntradaPorMilTokens(BigDecimal.ZERO)
                .custoSaidaPorMilTokens(BigDecimal.ZERO)
                .ativo(true)
                .build());
    }
}
