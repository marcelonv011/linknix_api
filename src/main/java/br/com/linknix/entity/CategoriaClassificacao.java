package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "categorias_classificacao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_categoria_classificacao_nome",
                        columnNames = "nome"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaClassificacao {

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
            name = "descricao",
            nullable = false,
            length = 500
    )
    private String descricao;

    @Column(
            name = "ativa",
            nullable = false
    )
    private Boolean ativa;

    @Column(
            name = "criada_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadaEm;

    @PrePersist
    public void prePersist() {
        if (ativa == null) {
            ativa = true;
        }

        if (criadaEm == null) {
            criadaEm = LocalDateTime.now();
        }
    }
}