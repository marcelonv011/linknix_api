package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "modelos_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "nome",
            nullable = false,
            length = 150
    )
    private String nome;

    @Column(
            name = "provedor",
            nullable = false,
            length = 100
    )
    private String provedor;

    @Column(
            name = "identificador_modelo",
            nullable = false,
            length = 150
    )
    private String identificadorModelo;

    @Column(
            name = "descricao",
            length = 500
    )
    private String descricao;

    @Column(
            name = "custo_entrada_por_mil_tokens",
            precision = 12,
            scale = 6
    )
    private BigDecimal custoEntradaPorMilTokens;

    @Column(
            name = "custo_saida_por_mil_tokens",
            precision = 12,
            scale = 6
    )
    private BigDecimal custoSaidaPorMilTokens;

    @Column(
            name = "ativo",
            nullable = false
    )
    private Boolean ativo;

    @Column(
            name = "criado_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    @Column(
            name = "atualizado_em"
    )
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        if (ativo == null) {
            ativo = true;
        }

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }

        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}