package br.com.linknix.service;

import br.com.linknix.dto.ModeloIAResponseDTO;
import br.com.linknix.entity.ModeloIA;
import br.com.linknix.entity.ProvedorIA;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ModeloIARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModeloIAService {

    private final ModeloIARepository modeloIARepository;

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

    private ModeloIAResponseDTO converterParaResponse(ModeloIA modelo) {
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
