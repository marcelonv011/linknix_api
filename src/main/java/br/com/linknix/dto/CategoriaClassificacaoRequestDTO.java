package br.com.linknix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaClassificacaoRequestDTO {
    @NotBlank @Size(max = 100)
    private String nome;
    @NotBlank @Size(max = 500)
    private String descricao;
    private Boolean ativa;
}
