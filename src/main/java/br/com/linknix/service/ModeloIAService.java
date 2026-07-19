package br.com.linknix.service;

import br.com.linknix.dto.ModeloIARequestDTO;
import br.com.linknix.dto.ModeloIAResponseDTO;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.ProvedorIA;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.exception.RegraNegocioException;
import br.com.linknix.repository.ModeloIARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ModeloIAService {

    private final ModeloIARepository modeloIARepository;
    private final ProvedorIAService provedorIAService;

    @Transactional
    public ModeloIAResponseDTO criar(ModeloIARequestDTO request) {
        ProvedorIA provedor = provedorIAService.buscarEntidadePorId(
                request.getProvedorId()
        );
        if (modeloIARepository.existsByProvedorIdAndIdentificadorModeloIgnoreCase(
                provedor.getId(),
                request.getIdentificadorModelo()
        )) {
            throw new ConflitoException(
                    "Este modelo já está cadastrado para o provedor"
            );
        }
        ModeloIA modelo = ModeloIA.builder()
                .nome(request.getNome().trim())
                .provedor(provedor)
                .identificadorModelo(request.getIdentificadorModelo().trim())
                .descricao(request.getDescricao())
                .custoEntradaPorMilTokens(request.getCustoEntradaPorMilTokens())
                .custoSaidaPorMilTokens(request.getCustoSaidaPorMilTokens())
                .ativo(request.getAtivo() == null || request.getAtivo())
                .build();
        return converterParaResponse(modeloIARepository.save(modelo));
    }

    @Transactional(readOnly = true)
    public List<ModeloIAResponseDTO> listarTodos() {
        return modeloIARepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    List<ModeloIA> listarEntidadesAtivas() {
        List<ModeloIA> modelos = modeloIARepository.findAllByAtivoTrueOrderByIdAsc();
        if (modelos.isEmpty()) {
            throw new RegraNegocioException("Nenhum modelo de IA está ativo");
        }
        return modelos;
    }

    List<ModeloIA> listarEntidadesAtivasSelecionadas(
            List<String> provedoresSelecionados
    ) {
        List<ModeloIA> modelosAtivos = listarEntidadesAtivas();
        if (provedoresSelecionados == null) {
            return modelosAtivos;
        }

        List<String> codigosNormalizados = provedoresSelecionados.stream()
                .map(this::normalizarCodigoProvedor)
                .toList();
        Set<String> codigosUnicos = new LinkedHashSet<>(codigosNormalizados);
        if (codigosUnicos.size() != codigosNormalizados.size()) {
            throw new RegraNegocioException(
                    "Nao repita o mesmo provedor de IA no chamado"
            );
        }

        for (String codigo : codigosUnicos) {
            boolean possuiModeloAtivo = modelosAtivos.stream()
                    .anyMatch(modelo -> modelo.getProvedor()
                            .getCodigo()
                            .equalsIgnoreCase(codigo));
            if (!possuiModeloAtivo) {
                throw new RegraNegocioException(
                        "O provedor " + codigo + " nao possui modelo ativo"
                );
            }
        }

        return modelosAtivos.stream()
                .filter(modelo -> codigosUnicos.contains(
                        normalizarCodigoProvedor(modelo.getProvedor().getCodigo())
                ))
                .toList();
    }

    @Transactional
    public ModeloIAResponseDTO atualizarAtivo(Long id, Boolean ativo) {
        ModeloIA modelo = buscarEntidadePorId(id);
        modelo.setAtivo(ativo);
        return converterParaResponse(modeloIARepository.save(modelo));
    }

    @Transactional(readOnly = true)
    public ModeloIAResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    ModeloIA buscarEntidadePorId(Long id) {
        return modeloIARepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Modelo de IA não encontrado com o ID " + id
                ));
    }

    private String normalizarCodigoProvedor(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new RegraNegocioException(
                    "O codigo do provedor de IA e obrigatorio"
            );
        }
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    ModeloIAResponseDTO converterParaResponse(ModeloIA modelo) {
        ProvedorIA provedor = modelo.getProvedor();

        return ModeloIAResponseDTO.builder()
                .id(modelo.getId())
                .nome(modelo.getNome())
                .provedorId(provedor.getId())
                .provedorNome(provedor.getNome())
                .provedorCodigo(provedor.getCodigo())
                .identificadorModelo(modelo.getIdentificadorModelo())
                .descricao(modelo.getDescricao())
                .custoEntradaPorMilTokens(modelo.getCustoEntradaPorMilTokens())
                .custoSaidaPorMilTokens(modelo.getCustoSaidaPorMilTokens())
                .ativo(modelo.getAtivo())
                .criadoEm(modelo.getCriadoEm())
                .atualizadoEm(modelo.getAtualizadoEm())
                .build();
    }
}
