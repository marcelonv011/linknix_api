package br.com.linknix.service;

import br.com.linknix.dto.MetricaClassificacaoResponseDTO;
import br.com.linknix.entity.MetricaClassificacao;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.MetricaClassificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricaClassificacaoService {

    private final MetricaClassificacaoRepository metricaClassificacaoRepository;

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
