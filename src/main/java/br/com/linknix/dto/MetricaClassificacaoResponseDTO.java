package br.com.linknix.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetricaClassificacaoResponseDTO {
    private Long id;
    private Long classificacaoIAId;
    private Boolean acertou;
    private LocalDateTime criadoEm;
}
