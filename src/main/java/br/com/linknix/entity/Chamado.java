package br.com.linknix.entity;

import br.com.linknix.enums.StatusChamado;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chamados",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chamado_cliente_codigo_externo",
                        columnNames = {
                                "cliente_helpdesk_id",
                                "codigo_externo"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "codigo_externo",
            nullable = false,
            length = 150
    )
    private String codigoExterno;

    @Column(
            name = "titulo",
            nullable = false,
            length = 255
    )
    private String titulo;

    @Column(
            name = "descricao",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String descricao;

    @Column(
            name = "sistema_origem",
            nullable = false,
            length = 150
    )
    private String sistemaOrigem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private StatusChamado status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "cliente_helpdesk_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_chamado_cliente_helpdesk"
            )
    )
    private ClienteHelpDesk clienteHelpDesk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "categoria_esperada_id",
            foreignKey = @ForeignKey(
                    name = "fk_chamado_categoria_esperada"
            )
    )
    private CategoriaClassificacao categoriaEsperada;

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
        if (status == null) {
            status = StatusChamado.RECEBIDO;
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
