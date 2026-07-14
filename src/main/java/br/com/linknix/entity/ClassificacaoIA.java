package br.com.linknix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "classificacoes_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificacaoIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "chamado_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_classificacao_ia_chamado"
            )
    )
    private Chamado chamado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "modelo_ia_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_classificacao_ia_modelo_ia"
            )
    )
    private ModeloIA modeloIA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "prompt_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_classificacao_ia_prompt"
            )
    )
    private Prompt prompt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "categoria_atribuida_id",
            foreignKey = @ForeignKey(
                    name = "fk_classificacao_ia_categoria_atribuida"
            )
    )
    private CategoriaClassificacao categoriaAtribuida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "execucao_teste_id",
            foreignKey = @ForeignKey(
                    name = "fk_classificacao_ia_execucao_teste"
            )
    )
    private ExecucaoTeste execucaoTeste;

    @Column(
            name = "nivel_confianca",
            precision = 5,
            scale = 4
    )
    private BigDecimal nivelConfianca;

    @Column(
            name = "justificativa",
            columnDefinition = "TEXT"
    )
    private String justificativa;

    @Column(
            name = "prompt_final",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String promptFinal;

    @Column(
            name = "resposta_bruta",
            columnDefinition = "TEXT"
    )
    private String respostaBruta;

    @Column(name = "tokens_entrada")
    private Integer tokensEntrada;

    @Column(name = "tokens_saida")
    private Integer tokensSaida;

    @Column(name = "tempo_resposta_ms")
    private Long tempoRespostaMs;

    @Column(
            name = "custo_estimado",
            precision = 14,
            scale = 6
    )
    private BigDecimal custoEstimado;

    @Column(
            name = "sucesso",
            nullable = false
    )
    private Boolean sucesso;

    @Column(
            name = "mensagem_erro",
            length = 1000
    )
    private String mensagemErro;

    @Column(
            name = "criada_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        if (sucesso == null) {
            sucesso = false;
        }

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
