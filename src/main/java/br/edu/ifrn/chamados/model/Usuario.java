package br.edu.ifrn.chamados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String nome;
    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String matricula;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;
    @OneToMany(mappedBy = "solicitante")
    private List<Chamado> chamadosAbertos = new ArrayList<>();
    @OneToMany(mappedBy = "tecnico")
    private List<Chamado> chamadosAtribuidos = new ArrayList<>();
    @OneToMany(mappedBy = "autor")
    private List<Interacao> interacoes = new ArrayList<>();

    public Usuario(String nome, String email, String matricula, TipoUsuario tipo, Setor setor) {
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.tipo = tipo;
        this.setor = setor;
    }
}
