package br.com.linknix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecucaoTesteRequestDTO {
    @NotBlank @Size(max = 150)
    private String nome;
    @Size(max = 500)
    private String descricao;
}
