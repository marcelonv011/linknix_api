package br.com.linknix.service;

import br.com.linknix.dto.ClassificacaoIAResponseDTO;
import br.com.linknix.dto.ValidacaoHumanaRequestDTO;
import br.com.linknix.dto.ValidacaoHumanaResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidacaoHumanaService {

    private final ChamadoService chamadoService;
    private final CategoriaClassificacaoService categoriaService;
    private final MetricaClassificacaoService metricaService;
    private final ClassificacaoIAService classificacaoIAService;

    @Transactional
    public ValidacaoHumanaResponseDTO validar(
            Long chamadoId,
            ValidacaoHumanaRequestDTO request
    ) {
        Chamado chamado = chamadoService.buscarEntidadePorId(chamadoId);
        CategoriaClassificacao categoria = categoriaService
                .buscarEntidadePorId(request.getCategoriaId());

        if (!Boolean.TRUE.equals(categoria.getAtiva())) {
            throw new RegraNegocioException(
                    "A categoria selecionada para validação humana está inativa"
            );
        }

        chamadoService.definirCategoriaEsperada(chamado, categoria);
        metricaService.avaliarClassificacoesDoChamado(chamado);

        List<ClassificacaoIAResponseDTO> classificacoes =
                classificacaoIAService.listarPorChamado(chamadoId);
        int totalAvaliadas = (int) classificacoes.stream()
                .filter(classificacao -> classificacao.getAcertou() != null)
                .count();
        int acertos = (int) classificacoes.stream()
                .filter(classificacao -> Boolean.TRUE.equals(classificacao.getAcertou()))
                .count();

        return ValidacaoHumanaResponseDTO.builder()
                .chamado(chamadoService.converterParaResponse(chamado))
                .classificacoes(classificacoes)
                .totalClassificacoesAvaliadas(totalAvaliadas)
                .totalAcertos(acertos)
                .build();
    }
}
