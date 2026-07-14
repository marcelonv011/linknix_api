package br.com.linknix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteHelpDeskRequestDTO {
    @NotBlank @Size(max = 150)
    private String nome;
    @NotBlank @Size(max = 150)
    private String sistemaOrigem;
    @NotBlank @Size(min = 32, max = 255)
    private String apiKey;
    private Long criadoPorUsuarioId;
    private Boolean ativo;
}
