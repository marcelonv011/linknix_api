package br.com.linknix.service;

import br.com.linknix.dto.ClassificacaoIAResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.ClassificacaoIA;
import br.com.linknix.entity.ExecucaoTeste;
import br.com.linknix.entity.MetricaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.Prompt;
import br.com.linknix.integration.RespostaLLM;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ClassificacaoIARepository;
import br.com.linknix.repository.MetricaClassificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassificacaoIAService {

    private final ClassificacaoIARepository classificacaoIARepository;
    private final MetricaClassificacaoRepository metricaClassificacaoRepository;

    ClassificacaoIA registrarSucesso(
            Chamado chamado,
            ModeloIA modelo,
            Prompt prompt,
            CategoriaClassificacao categoria,
            String promptFinal,
            RespostaLLM resposta
    ) {
        ClassificacaoIA classificacao = ClassificacaoIA.builder()
                .chamado(chamado)
                .modeloIA(modelo)
                .prompt(prompt)
                .categoriaAtribuida(categoria)
                .nivelConfianca(resposta.getNivelConfianca())
                .justificativa(resposta.getJustificativa())
                .promptFinal(promptFinal)
                .respostaBruta(resposta.getRespostaBruta())
                .tokensEntrada(resposta.getTokensEntrada())
                .tokensSaida(resposta.getTokensSaida())
                .tempoRespostaMs(resposta.getTempoRespostaMs())
                .custoEstimado(calcularCusto(modelo, resposta))
                .sucesso(true)
                .build();
        return classificacaoIARepository.save(classificacao);
    }

    @Transactional(readOnly = true)
    public List<ClassificacaoIAResponseDTO> listarPorChamado(Long chamadoId) {
        return classificacaoIARepository
                .findAllByChamadoIdOrderByCriadoEmAsc(chamadoId)
                .stream()
                .map(classificacao -> converterParaResponse(
                        classificacao,
                        metricaClassificacaoRepository
                                .findByClassificacaoIAId(classificacao.getId())
                                .orElse(null)
                ))
                .toList();
    }

    List<ClassificacaoIAResponseDTO> converterListaParaResponse(
            List<ClassificacaoIA> classificacoes
    ) {
        return classificacoes.stream()
                .map(classificacao -> converterParaResponse(classificacao, null))
                .toList();
    }

    private BigDecimal calcularCusto(ModeloIA modelo, RespostaLLM resposta) {
        BigDecimal custoEntrada = modelo.getCustoEntradaPorMilTokens() == null
                ? BigDecimal.ZERO
                : modelo.getCustoEntradaPorMilTokens();
        BigDecimal custoSaida = modelo.getCustoSaidaPorMilTokens() == null
                ? BigDecimal.ZERO
                : modelo.getCustoSaidaPorMilTokens();
        BigDecimal entrada = custoEntrada.multiply(
                BigDecimal.valueOf(resposta.getTokensEntrada())
        );
        BigDecimal saida = custoSaida.multiply(
                BigDecimal.valueOf(resposta.getTokensSaida())
        );
        return entrada.add(saida)
                .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public ClassificacaoIAResponseDTO buscarPorId(Long id) {
        ClassificacaoIA classificacao = classificacaoIARepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Classificação de IA não encontrada com o ID " + id
                ));
        MetricaClassificacao metrica = metricaClassificacaoRepository
                .findByClassificacaoIAId(id)
                .orElse(null);

        return converterParaResponse(classificacao, metrica);
    }

    ClassificacaoIAResponseDTO converterParaResponse(
            ClassificacaoIA classificacao,
            MetricaClassificacao metrica
    ) {
        CategoriaClassificacao categoria = classificacao.getCategoriaAtribuida();
        ExecucaoTeste execucaoTeste = classificacao.getExecucaoTeste();

        return ClassificacaoIAResponseDTO.builder()
                .id(classificacao.getId())
                .chamadoId(classificacao.getChamado().getId())
                .modeloIAId(classificacao.getModeloIA().getId())
                .modeloIANome(classificacao.getModeloIA().getNome())
                .provedorCodigo(classificacao.getModeloIA().getProvedor().getCodigo())
                .promptId(classificacao.getPrompt().getId())
                .promptNome(classificacao.getPrompt().getNome())
                .promptVersao(classificacao.getPrompt().getVersao())
                .categoriaAtribuidaId(categoria == null ? null : categoria.getId())
                .categoriaAtribuidaNome(categoria == null ? null : categoria.getNome())
                .execucaoTesteId(execucaoTeste == null ? null : execucaoTeste.getId())
                .nivelConfianca(classificacao.getNivelConfianca())
                .justificativa(classificacao.getJustificativa())
                .promptFinal(classificacao.getPromptFinal())
                .respostaBruta(classificacao.getRespostaBruta())
                .tokensEntrada(classificacao.getTokensEntrada())
                .tokensSaida(classificacao.getTokensSaida())
                .tempoRespostaMs(classificacao.getTempoRespostaMs())
                .custoEstimado(classificacao.getCustoEstimado())
                .sucesso(classificacao.getSucesso())
                .mensagemErro(classificacao.getMensagemErro())
                .metricaClassificacaoId(metrica == null ? null : metrica.getId())
                .acertou(metrica == null ? null : metrica.getAcertou())
                .criadoEm(classificacao.getCriadoEm())
                .build();
    }
}
