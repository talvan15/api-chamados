package br.edu.ifrn.chamados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "setores", uniqueConstraints = @UniqueConstraint(columnNames = "sigla"))
@Getter
@Setter
@NoArgsConstructor
public class Setor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String nome;
    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String sigla;
    @OneToMany(mappedBy = "setor")
    private List<Categoria> categorias = new ArrayList<>();
    @OneToMany(mappedBy = "setor")
    private List<Usuario> usuarios = new ArrayList<>();

    public Setor(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }
}
