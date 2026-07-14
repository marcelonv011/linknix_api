package br.com.linknix.entity;

import br.com.linknix.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

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
            name = "email",
            nullable = false,
            length = 180
    )
    private String email;

    @Column(
            name = "senha_hash",
            nullable = false,
            length = 255
    )
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "perfil",
            nullable = false,
            length = 30
    )
    private PerfilUsuario perfil;

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

    @PrePersist
    public void prePersist() {
        if (ativo == null) {
            ativo = true;
        }

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}