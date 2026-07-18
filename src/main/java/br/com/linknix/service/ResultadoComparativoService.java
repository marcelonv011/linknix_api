package br.com.linknix.service;

import br.com.linknix.dto.ResultadoComparativoResponseDTO;
import br.com.linknix.entity.ResultadoComparativo;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ClassificacaoIA;
import br.com.linknix.entity.CriterioComparacao;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ResultadoComparativoRepository;
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
public class ResultadoComparativoService {

    private final ResultadoComparativoRepository resultadoComparativoRepository;
    private final CriterioComparacaoService criterioComparacaoService;

    ResultadoComparativo gerar(
            Chamado chamado,
            List<ClassificacaoIA> classificacoes
    ) {
        if (classificacoes.isEmpty()) {
            throw new IllegalArgumentException(
                    "É necessária ao menos uma classificação bem-sucedida"
            );
        }
        Map<CategoriaClassificacao, Long> votos = classificacoes.stream()
                .collect(Collectors.groupingBy(
                        ClassificacaoIA::getCategoriaAtribuida,
                        Collectors.counting()
                ));
        CategoriaClassificacao categoriaFinal = votos.entrySet().stream()
                .max(Comparator
                        .<Map.Entry<CategoriaClassificacao, Long>>comparingLong(Map.Entry::getValue)
                        .thenComparing(entry -> entry.getKey().getNome()))
                .orElseThrow()
                .getKey();
        int concordantes = votos.get(categoriaFinal).intValue();
        BigDecimal percentual = BigDecimal.valueOf(concordantes)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(classificacoes.size()),
                        2,
                        RoundingMode.HALF_UP
                );
        CriterioComparacao criterio = criterioComparacaoService.buscarEntidadeAtiva();
        ResultadoComparativo resultado = ResultadoComparativo.builder()
                .chamado(chamado)
                .categoriaFinal(categoriaFinal)
                .criterioUtilizado(criterio)
                .totalModelos(classificacoes.size())
                .quantidadeConcordante(concordantes)
                .percentualConcordancia(percentual)
                .build();
        return resultadoComparativoRepository.save(resultado);
    }

    @Transactional(readOnly = true)
    public ResultadoComparativoResponseDTO buscarPorId(Long id) {
        ResultadoComparativo resultado = resultadoComparativoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Resultado comparativo não encontrado com o ID " + id
                ));

        return converterParaResponse(resultado);
    }

    @Transactional(readOnly = true)
    public ResultadoComparativoResponseDTO buscarPorChamado(Long chamadoId) {
        ResultadoComparativo resultado = resultadoComparativoRepository
                .findByChamadoId(chamadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Resultado não encontrado para o chamado " + chamadoId
                ));
        return converterParaResponse(resultado);
    }

    ResultadoComparativoResponseDTO converterParaResponse(
            ResultadoComparativo resultado
    ) {
        return ResultadoComparativoResponseDTO.builder()
                .id(resultado.getId())
                .chamadoId(resultado.getChamado().getId())
                .categoriaFinalId(resultado.getCategoriaFinal().getId())
                .categoriaFinalNome(resultado.getCategoriaFinal().getNome())
                .criterioUtilizadoId(resultado.getCriterioUtilizado().getId())
                .criterioUtilizadoNome(resultado.getCriterioUtilizado().getNome())
                .criterioUtilizadoCodigo(resultado.getCriterioUtilizado().getCodigo())
                .totalModelos(resultado.getTotalModelos())
                .quantidadeConcordante(resultado.getQuantidadeConcordante())
                .percentualConcordancia(resultado.getPercentualConcordancia())
                .criadoEm(resultado.getCriadoEm())
                .build();
    }
}
