package br.com.linknix.service;

import br.com.linknix.dto.ProvedorIAResponseDTO;
import br.com.linknix.entity.ProvedorIA;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.ProvedorIARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProvedorIAService {

    private final ProvedorIARepository provedorIARepository;

    @Transactional(readOnly = true)
    public ProvedorIAResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    ProvedorIA buscarEntidadePorId(Long id) {
        return provedorIARepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Provedor de IA não encontrado com o ID " + id
                ));
    }

    private ProvedorIAResponseDTO converterParaResponse(ProvedorIA provedor) {
        return ProvedorIAResponseDTO.builder()
                .id(provedor.getId())
                .nome(provedor.getNome())
                .codigo(provedor.getCodigo())
                .descricao(provedor.getDescricao())
                .ativo(provedor.getAtivo())
                .criadoEm(provedor.getCriadoEm())
                .atualizadoEm(provedor.getAtualizadoEm())
                .build();
    }
}
