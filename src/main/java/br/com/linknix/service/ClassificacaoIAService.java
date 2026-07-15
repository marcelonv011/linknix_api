package br.com.linknix.service;

import br.com.linknix.dto.ClassificacaoIAResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.ClassificacaoIA;
import br.com.linknix.entity.ExecucaoTeste;
import br.com.linknix.entity.MetricaClassificacao;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ClassificacaoIARepository;
import br.com.linknix.repository.MetricaClassificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassificacaoIAService {

    private final ClassificacaoIARepository classificacaoIARepository;
    private final MetricaClassificacaoRepository metricaClassificacaoRepository;

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

    private ClassificacaoIAResponseDTO converterParaResponse(
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
