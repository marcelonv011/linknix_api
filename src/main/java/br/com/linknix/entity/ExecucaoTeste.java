package br.com.linknix.entity;

import br.com.linknix.enums.StatusExecucaoTeste;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "execucoes_teste")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecucaoTeste {

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

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private StatusExecucaoTeste status;

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
        if (status == null) {
            status = StatusExecucaoTeste.PENDENTE;
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
