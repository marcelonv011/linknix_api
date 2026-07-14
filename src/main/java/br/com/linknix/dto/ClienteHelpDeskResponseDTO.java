package br.com.linknix.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteHelpDeskResponseDTO {
    private Long id;
    private String nome;
    private String sistemaOrigem;
    private String apiKeyMascarada;
    private Long criadoPorUsuarioId;
    private String criadoPorUsuarioNome;
    private Boolean ativo;
    private LocalDateTime criadoEm;
}
