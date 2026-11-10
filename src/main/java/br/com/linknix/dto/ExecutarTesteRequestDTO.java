package br.com.linknix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutarTesteRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String titulo;

    @NotBlank
    private String descricao;

    @NotEmpty(message = "Informe ao menos um provedor de IA")
    private List<@NotBlank String> provedoresIA;
}
