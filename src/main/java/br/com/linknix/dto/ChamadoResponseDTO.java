package br.com.linknix.dto;

import br.com.linknix.enums.StatusChamado;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChamadoResponseDTO {
    private Long id;
    private String codigoExterno;
    private String titulo;
    private String descricao;
    private String sistemaOrigem;
    private StatusChamado status;
    private Long clienteHelpDeskId;
    private String clienteHelpDeskNome;
    private Long categoriaEsperadaId;
    private String categoriaEsperadaNome;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
