package br.edu.ifrn.chamados.service;

import br.edu.ifrn.chamados.api.ApiException;
import br.edu.ifrn.chamados.api.PageSupport;
import br.edu.ifrn.chamados.api.dto.*;
import br.edu.ifrn.chamados.model.*;
import br.edu.ifrn.chamados.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CadastroService {
    private final SetorRepository setores;
    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;
    private final ChamadoRepository chamados;

    public CadastroService(SetorRepository s, CategoriaRepository c, UsuarioRepository u, ChamadoRepository ch) {
        setores = s;
        categorias = c;
        usuarios = u;
        chamados = ch;
    }

    private <T> T require(Optional<T> o, String what) {
        return o.orElseThrow(() -> ApiException.naoEncontrado(what + " não encontrado(a)."));
    }

    private static Specification<Setor> setorNome(String nome) {
        return (r, q, b) -> nome == null || nome.isBlank() ? null : b.like(b.lower(r.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    private static Specification<Categoria> categoriaFiltros(String nome, Long setorId) {
        return (r, q, b) -> {
            var p = b.conjunction();
            if (nome != null && !nome.isBlank())
                p = b.and(p, b.like(b.lower(r.get("nome")), "%" + nome.toLowerCase() + "%"));
            if (setorId != null) p = b.and(p, b.equal(r.get("setor").get("id"), setorId));
            return p;
        };
    }

    private static Specification<Usuario> usuarioFiltros(String nome, TipoUsuario tipo, Long setorId) {
        return (r, q, b) -> {
            var p = b.conjunction();
            if (nome != null && !nome.isBlank())
                p = b.and(p, b.like(b.lower(r.get("nome")), "%" + nome.toLowerCase() + "%"));
            if (tipo != null) p = b.and(p, b.equal(r.get("tipo"), tipo));
            if (setorId != null) p = b.and(p, b.equal(r.get("setor").get("id"), setorId));
            return p;
        };
    }

    @Transactional(readOnly = true)
    public Page<SetorResponse> listarSetores(String nome, Pageable p) {
        return setores.findAll(setorNome(nome), PageSupport.cap(p)).map(SetorResponse::from);
    }

    @Transactional(readOnly = true)
    public SetorResponse setor(Long id) {
        return SetorResponse.from(require(setores.findById(id), "Setor"));
    }

    public SetorResponse criarSetor(SetorRequest r) {
        if (setores.existsBySiglaIgnoreCase(r.sigla())) throw ApiException.conflito("Já existe setor com esta sigla.");
        return SetorResponse.from(setores.save(new Setor(r.nome().trim(), r.sigla().trim().toUpperCase())));
    }

    public SetorResponse atualizarSetor(Long id, SetorRequest r) {
        var s = require(setores.findById(id), "Setor");
        if (!s.getSigla().equalsIgnoreCase(r.sigla()) && setores.existsBySiglaIgnoreCase(r.sigla()))
            throw ApiException.conflito("Já existe setor com esta sigla.");
        s.setNome(r.nome().trim());
        s.setSigla(r.sigla().trim().toUpperCase());
        return SetorResponse.from(s);
    }

    public void excluirSetor(Long id) {
        var s = require(setores.findById(id), "Setor");
        if (categorias.existsBySetorId(id) || usuarios.existsBySetorId(id))
            throw ApiException.conflito("Remova primeiro as categorias e técnicos vinculados ao setor.");
        setores.delete(s);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarCategorias(String nome, Long setorId, Pageable p) {
        return categorias.findAll(categoriaFiltros(nome, setorId), PageSupport.cap(p)).map(CategoriaResponse::from);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse categoria(Long id) {
        return CategoriaResponse.from(require(categorias.findById(id), "Categoria"));
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> categoriasDoSetor(Long id, Pageable p) {
        if (!setores.existsById(id)) throw ApiException.naoEncontrado("Setor não encontrado.");
        return listarCategorias(null, id, p);
    }

    public CategoriaResponse criarCategoria(CategoriaRequest r) {
        var s = require(setores.findById(r.setorId()), "Setor");
        return CategoriaResponse.from(categorias.save(new Categoria(r.nome().trim(), r.descricao(), s)));
    }

    public CategoriaResponse atualizarCategoria(Long id, CategoriaRequest r) {
        var c = require(categorias.findById(id), "Categoria");
        c.setNome(r.nome().trim());
        c.setDescricao(r.descricao());
        c.setSetor(require(setores.findById(r.setorId()), "Setor"));
        return CategoriaResponse.from(c);
    }

    public void excluirCategoria(Long id) {
        var c = require(categorias.findById(id), "Categoria");
        if (chamados.existsByCategoriaId(id))
            throw ApiException.conflito("Não é permitido excluir categoria com chamados vinculados.");
        categorias.delete(c);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuarios(String nome, TipoUsuario tipo, Long setorId, Pageable p) {
        return usuarios.findAll(usuarioFiltros(nome, tipo, setorId), PageSupport.cap(p)).map(UsuarioResponse::from);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse usuario(Long id) {
        return UsuarioResponse.from(require(usuarios.findById(id), "Usuário"));
    }

    public UsuarioResponse criarUsuario(UsuarioRequest r) {
        if (usuarios.existsByEmailIgnoreCase(r.email()))
            throw ApiException.conflito("Já existe usuário com este e-mail.");
        if (usuarios.existsByMatriculaIgnoreCase(r.matricula()))
            throw ApiException.conflito("Já existe usuário com esta matrícula.");
        return UsuarioResponse.from(usuarios.save(montarUsuario(r)));
    }

    public UsuarioResponse atualizarUsuario(Long id, UsuarioRequest r) {
        var u = require(usuarios.findById(id), "Usuário");
        if (!u.getEmail().equalsIgnoreCase(r.email()) && usuarios.existsByEmailIgnoreCase(r.email()))
            throw ApiException.conflito("Já existe usuário com este e-mail.");
        if (!u.getMatricula().equalsIgnoreCase(r.matricula()) && usuarios.existsByMatriculaIgnoreCase(r.matricula()))
            throw ApiException.conflito("Já existe usuário com esta matrícula.");
        var novo = montarUsuario(r);
        u.setNome(novo.getNome());
        u.setEmail(novo.getEmail());
        u.setMatricula(novo.getMatricula());
        u.setTipo(novo.getTipo());
        u.setSetor(novo.getSetor());
        return UsuarioResponse.from(u);
    }

    private Usuario montarUsuario(UsuarioRequest r) {
        if (r.tipo() == TipoUsuario.TECNICO && r.setorId() == null)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Técnico precisa pertencer a um setor.");
        Setor s = r.setorId() == null ? null : require(setores.findById(r.setorId()), "Setor");
        return new Usuario(r.nome().trim(), r.email().trim().toLowerCase(), r.matricula().trim(), r.tipo(), s);
    }
}
