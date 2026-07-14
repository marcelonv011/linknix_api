package br.com.linknix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CriterioComparacaoRequestDTO {
    @NotBlank @Size(max = 150)
    private String nome;
    @NotBlank @Size(max = 100)
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "deve conter apenas letras maiúsculas, números e underscore")
    private String codigo;
    @NotBlank @Size(max = 500)
    private String descricao;
    private Boolean ativo;
}
