package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "clientes_helpdesk",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cliente_helpdesk_api_key",
                        columnNames = "api_key"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteHelpDesk {

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
            name = "sistema_origem",
            nullable = false,
            length = 150
    )
    private String sistemaOrigem;

    @Column(
            name = "api_key",
            nullable = false,
            length = 255
    )
    private String apiKey;

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