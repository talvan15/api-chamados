package br.edu.ifrn.chamados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interacoes")
@Getter
@Setter
@NoArgsConstructor
public class Interacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String texto;
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @PrePersist
    void prePersist() {
        if (dataHora == null) dataHora = LocalDateTime.now();
    }

    public Interacao(String texto, Chamado chamado, Usuario autor) {
        this.texto = texto;
        this.chamado = chamado;
        this.autor = autor;
    }
}
