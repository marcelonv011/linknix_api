package br.com.linknix.service;

import br.com.linknix.dto.ChamadoRequestDTO;
import br.com.linknix.dto.ChamadoResponseDTO;
import br.com.linknix.entity.CategoriaClassificacao;
import br.com.linknix.entity.Chamado;
import br.com.linknix.entity.ClienteHelpDesk;
import br.com.linknix.enums.StatusChamado;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.repository.ChamadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final ClienteHelpDeskService clienteHelpDeskService;

    @Transactional
    public ChamadoResponseDTO receber(
            String apiKey,
            ChamadoRequestDTO chamadoRequest
    ) {
        ClienteHelpDesk clienteHelpDesk = clienteHelpDeskService
                .buscarAtivoPorApiKey(apiKey);
        String codigoExterno = chamadoRequest.getCodigoExterno().trim();

        if (chamadoRepository.existsByClienteHelpDeskIdAndCodigoExterno(
                clienteHelpDesk.getId(),
                codigoExterno
        )) {
            throw new ConflitoException(
                    "Já existe um chamado com o código externo "
                            + codigoExterno
                            + " para este cliente Help Desk"
            );
        }

        Chamado chamado = Chamado.builder()
                .codigoExterno(codigoExterno)
                .titulo(chamadoRequest.getTitulo().trim())
                .descricao(chamadoRequest.getDescricao().trim())
                .sistemaOrigem(clienteHelpDesk.getSistemaOrigem())
                .status(StatusChamado.RECEBIDO)
                .clienteHelpDesk(clienteHelpDesk)
                .build();

        return converterParaResponse(chamadoRepository.save(chamado));
    }

    private ChamadoResponseDTO converterParaResponse(Chamado chamado) {
        ClienteHelpDesk clienteHelpDesk = chamado.getClienteHelpDesk();
        CategoriaClassificacao categoriaEsperada = chamado.getCategoriaEsperada();

        return ChamadoResponseDTO.builder()
                .id(chamado.getId())
                .codigoExterno(chamado.getCodigoExterno())
                .titulo(chamado.getTitulo())
                .descricao(chamado.getDescricao())
                .sistemaOrigem(chamado.getSistemaOrigem())
                .status(chamado.getStatus())
                .clienteHelpDeskId(clienteHelpDesk.getId())
                .clienteHelpDeskNome(clienteHelpDesk.getNome())
                .categoriaEsperadaId(
                        categoriaEsperada == null ? null : categoriaEsperada.getId()
                )
                .categoriaEsperadaNome(
                        categoriaEsperada == null ? null : categoriaEsperada.getNome()
                )
                .criadoEm(chamado.getCriadoEm())
                .atualizadoEm(chamado.getAtualizadoEm())
                .build();
    }
}
