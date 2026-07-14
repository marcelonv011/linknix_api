package br.com.linknix.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PromptResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String conteudo;
    private Integer versao;
    private Boolean ativo;
    private String autor;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
