package br.com.linknix.service;

import br.com.linknix.dto.ChamadoRequestDTO;
import br.com.linknix.dto.ProcessamentoChamadoResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ClassificacaoIA;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.Prompt;
import br.com.linknix.entity.ResultadoComparativo;
import br.com.linknix.enums.StatusChamado;
import br.com.linknix.exception.IntegracaoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.integration.LLMProvider;
import br.com.linknix.integration.LLMProviderRegistry;
import br.com.linknix.integration.RespostaLLM;
import br.com.linknix.integration.SolicitacaoLLM;
import br.com.linknix.promptengine.ContextoPrompt;
import br.com.linknix.promptengine.PromptEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessamentoChamadoService {

    private final ChamadoService chamadoService;
    private final PromptService promptService;
    private final CategoriaClassificacaoService categoriaService;
    private final ModeloIAService modeloIAService;
    private final ClassificacaoIAService classificacaoIAService;
    private final MetricaClassificacaoService metricaService;
    private final ResultadoComparativoService resultadoService;
    private final PromptEngine promptEngine;
    private final LLMProviderRegistry providerRegistry;

    @Transactional(noRollbackFor = {
            IntegracaoException.class,
            RegraNegocioException.class
    })
    public ProcessamentoChamadoResponseDTO receberEClassificar(
            String apiKey,
            ChamadoRequestDTO request
    ) {
        List<ModeloIA> modelos = modeloIAService
                .listarEntidadesAtivasSelecionadas(request.getProvedoresIA());
        Chamado chamado = chamadoService.receberEntidade(apiKey, request);
        chamadoService.atualizarStatus(chamado, StatusChamado.EM_PROCESSAMENTO);

        try {
            return classificar(chamado, modelos);
        } catch (IntegracaoException | RegraNegocioException exception) {
            chamadoService.atualizarStatus(chamado, StatusChamado.ERRO);
            throw exception;
        }
    }

    private ProcessamentoChamadoResponseDTO classificar(
            Chamado chamado,
            List<ModeloIA> modelos
    ) {
        Prompt prompt = promptService.buscarEntidadeAtiva();
        List<CategoriaClassificacao> categorias = categoriaService
                .listarEntidadesAtivas();
        List<String> nomesCategorias = categorias.stream()
                .map(CategoriaClassificacao::getNome)
                .toList();
        String promptFinal = promptEngine.montar(
                prompt.getConteudo(),
                ContextoPrompt.builder()
                        .titulo(chamado.getTitulo())
                        .descricao(chamado.getDescricao())
                        .categorias(nomesCategorias)
                        .sistemaOrigem(chamado.getSistemaOrigem())
                        .build()
        );

        List<ClassificacaoIA> classificacoes = new ArrayList<>();
        for (ModeloIA modelo : modelos) {
            LLMProvider provider = providerRegistry.obter(
                    modelo.getProvedor().getCodigo()
            );
            RespostaLLM resposta = provider.executar(SolicitacaoLLM.builder()
                    .identificadorModelo(modelo.getIdentificadorModelo())
                    .promptFinal(promptFinal)
                    .categoriasDisponiveis(nomesCategorias)
                    .build());
            CategoriaClassificacao categoria = categoriaService
                    .buscarAtivaPorNome(resposta.getCategoriaSugerida());
            ClassificacaoIA classificacao = classificacaoIAService
                    .registrarSucesso(
                            chamado,
                            modelo,
                            prompt,
                            categoria,
                            promptFinal,
                            resposta
                    );
            metricaService.registrarSeAplicavel(classificacao);
            classificacoes.add(classificacao);
        }

        ResultadoComparativo resultado = resultadoService.gerar(
                chamado,
                classificacoes
        );
        chamadoService.atualizarStatus(chamado, StatusChamado.CLASSIFICADO);

        return ProcessamentoChamadoResponseDTO.builder()
                .chamado(chamadoService.converterParaResponse(chamado))
                .classificacoes(
                        classificacaoIAService.converterListaParaResponse(
                                classificacoes
                        )
                )
                .resultado(resultadoService.converterParaResponse(resultado))
                .build();
    }
}
