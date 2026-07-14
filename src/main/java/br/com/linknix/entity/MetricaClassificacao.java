package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "metricas_classificacao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_metrica_classificacao_classificacao_ia",
                        columnNames = "classificacao_ia_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricaClassificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "classificacao_ia_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_metrica_classificacao_classificacao_ia"
            )
    )
    private ClassificacaoIA classificacaoIA;

    @Column(
            name = "acertou",
            nullable = false
    )
    private Boolean acertou;

    @Column(
            name = "criado_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
