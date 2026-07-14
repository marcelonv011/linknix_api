package br.com.linknix.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaClassificacaoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativa;
    private LocalDateTime criadaEm;
}
