package br.edu.ifrn.chamados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String nome;
    @Column(columnDefinition = "text")
    private String descricao;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;
    @OneToMany(mappedBy = "categoria")
    private List<Chamado> chamados = new ArrayList<>();

    public Categoria(String nome, String descricao, Setor setor) {
        this.nome = nome;
        this.descricao = descricao;
        this.setor = setor;
    }
}
