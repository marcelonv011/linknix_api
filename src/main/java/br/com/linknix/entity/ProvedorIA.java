package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "provedores_ia",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_provedor_ia_nome",
                        columnNames = "nome"
                ),
                @UniqueConstraint(
                        name = "uk_provedor_ia_codigo",
                        columnNames = "codigo"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvedorIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "nome",
            nullable = false,
            length = 100
    )
    private String nome;

    @Column(
            name = "codigo",
            nullable = false,
            length = 50
    )
    private String codigo;

    @Column(
            name = "descricao",
            length = 500
    )
    private String descricao;

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

    @Column(name = "atualizado_em")
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
