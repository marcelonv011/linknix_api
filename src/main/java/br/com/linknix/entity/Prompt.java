package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prompts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prompt {

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
            name = "descricao",
            length = 500
    )
    private String descricao;

    @Lob
    @Column(
            name = "conteudo",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String conteudo;

    @Column(
            name = "versao",
            nullable = false
    )
    private Integer versao;

    @Column(
            name = "ativo",
            nullable = false
    )
    private Boolean ativo;

    @Column(
            name = "autor",
            nullable = false,
            length = 150
    )
    private String autor;

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
        if (versao == null) {
            versao = 1;
        }

        if (ativo == null) {
            ativo = false;
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