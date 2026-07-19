package br.com.linknix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChamadoRequestDTO {
    @NotBlank @Size(max = 150)
    private String codigoExterno;
    @NotBlank @Size(max = 255)
    private String titulo;
    @NotBlank
    private String descricao;
    @Size(min = 1, message = "Informe ao menos um provedor de IA")
    private List<@NotBlank String> provedoresIA;
}
