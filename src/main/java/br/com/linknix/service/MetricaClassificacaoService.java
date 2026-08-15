package br.com.linknix.service;

import br.com.linknix.dto.MetricaClassificacaoResponseDTO;
import br.com.linknix.dto.DesempenhoModeloResponseDTO;
import br.com.linknix.entity.MetricaClassificacao;
import br.com.linknix.entity.ClassificacaoIA;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ClassificacaoIARepository;
import br.com.linknix.repository.MetricaClassificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricaClassificacaoService {

    private final MetricaClassificacaoRepository metricaClassificacaoRepository;
    private final ClassificacaoIARepository classificacaoIARepository;

    void registrarSeAplicavel(ClassificacaoIA classificacao) {
        if (classificacao.getChamado().getCategoriaEsperada() == null
                || classificacao.getCategoriaAtribuida() == null) {
            return;
        }
        boolean acertou = classificacao.getChamado()
                .getCategoriaEsperada().getId()
                .equals(classificacao.getCategoriaAtribuida().getId());
        salvarAvaliacao(classificacao, acertou);
    }

    void avaliarClassificacoesDoChamado(Chamado chamado) {
        classificacaoIARepository
                .findAllByChamadoIdOrderByCriadoEmAsc(chamado.getId())
                .stream()
                .filter(classificacao -> classificacao.getCategoriaAtribuida() != null)
                .forEach(classificacao -> {
                    boolean acertou = chamado.getCategoriaEsperada().getId()
                            .equals(classificacao.getCategoriaAtribuida().getId());
                    salvarAvaliacao(classificacao, acertou);
                });
    }

    private void salvarAvaliacao(ClassificacaoIA classificacao, boolean acertou) {
        MetricaClassificacao metrica = metricaClassificacaoRepository
                .findByClassificacaoIAId(classificacao.getId())
                .orElseGet(() -> MetricaClassificacao.builder()
                        .classificacaoIA(classificacao)
                        .build());
        metrica.setAcertou(acertou);
        metricaClassificacaoRepository.save(metrica);
    }

    @Transactional(readOnly = true)
    public List<MetricaClassificacaoResponseDTO> listarTodas() {
        return metricaClassificacaoRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DesempenhoModeloResponseDTO> calcularDesempenhoPorModelo() {
        Map<ModeloIA, List<MetricaClassificacao>> porModelo =
                metricaClassificacaoRepository.findAll().stream()
                        .collect(Collectors.groupingBy(
                                metrica -> metrica.getClassificacaoIA().getModeloIA()
                        ));

        return porModelo.entrySet().stream()
                .map(entry -> converterDesempenho(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DesempenhoModeloResponseDTO::getModeloIANome))
                .toList();
    }

    private DesempenhoModeloResponseDTO converterDesempenho(
            ModeloIA modelo,
            List<MetricaClassificacao> metricas
    ) {
        int total = metricas.size();
        int acertos = (int) metricas.stream()
                .filter(metrica -> Boolean.TRUE.equals(metrica.getAcertou()))
                .count();
        BigDecimal taxa = total == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(acertos)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        return DesempenhoModeloResponseDTO.builder()
                .modeloIAId(modelo.getId())
                .modeloIANome(modelo.getNome())
                .provedorCodigo(modelo.getProvedor().getCodigo())
                .totalAvaliacoes(total)
                .totalAcertos(acertos)
                .taxaAcerto(taxa)
                .build();
    }

    @Transactional(readOnly = true)
    public MetricaClassificacaoResponseDTO buscarPorId(Long id) {
        MetricaClassificacao metrica = metricaClassificacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Métrica de classificação não encontrada com o ID " + id
                ));

        return converterParaResponse(metrica);
    }

    private MetricaClassificacaoResponseDTO converterParaResponse(
            MetricaClassificacao metrica
    ) {
        return MetricaClassificacaoResponseDTO.builder()
                .id(metrica.getId())
                .classificacaoIAId(metrica.getClassificacaoIA().getId())
                .acertou(metrica.getAcertou())
                .criadoEm(metrica.getCriadoEm())
                .build();
    }
}
