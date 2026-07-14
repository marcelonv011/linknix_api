package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "resultados_comparativos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_resultado_comparativo_chamado",
                        columnNames = "chamado_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoComparativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "chamado_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resultado_comparativo_chamado"
            )
    )
    private Chamado chamado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_final_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resultado_comparativo_categoria_final"
            )
    )
    private CategoriaClassificacao categoriaFinal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "criterio_comparacao_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resultado_comparativo_criterio_comparacao"
            )
    )
    private CriterioComparacao criterioUtilizado;

    @Column(
            name = "total_modelos",
            nullable = false
    )
    private Integer totalModelos;

    @Column(
            name = "quantidade_concordante",
            nullable = false
    )
    private Integer quantidadeConcordante;

    @Column(
            name = "percentual_concordancia",
            precision = 5,
            scale = 2,
            nullable = false
    )
    private BigDecimal percentualConcordancia;

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
