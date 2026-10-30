package br.edu.ifrn.chamados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "chamados")
@Getter
@Setter
@NoArgsConstructor
public class Chamado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String titulo;
    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String descricao;
    @NotBlank
    @Column(nullable = false)
    private String local;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridade prioridade = Prioridade.MEDIA;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusChamado status = StatusChamado.ABERTO;
    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;
    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;
    @Column(columnDefinition = "text")
    private String solucao;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;
    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataHora ASC")
    private List<Interacao> interacoes = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dataAbertura == null) dataAbertura = LocalDateTime.now();
        if (prioridade == null) prioridade = Prioridade.MEDIA;
        if (status == null) status = StatusChamado.ABERTO;
    }
}
