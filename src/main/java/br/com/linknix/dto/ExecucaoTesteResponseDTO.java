package br.com.linknix.dto;

import br.com.linknix.enums.StatusExecucaoTeste;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecucaoTesteResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private StatusExecucaoTeste status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
