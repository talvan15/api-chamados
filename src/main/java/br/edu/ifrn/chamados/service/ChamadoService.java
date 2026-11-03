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

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ChamadoService {
    private final ChamadoRepository chamados;
    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final InteracaoRepository interacoes;

    public ChamadoService(ChamadoRepository c, UsuarioRepository u, CategoriaRepository ca, InteracaoRepository i) {
        chamados = c;
        usuarios = u;
        categorias = ca;
        interacoes = i;
    }

    private <T> T req(Optional<T> o, String n) {
        return o.orElseThrow(() -> ApiException.naoEncontrado(n + " não encontrado(a)."));
    }

    private static Specification<Chamado> filtros(StatusChamado status, Prioridade prioridade, Long setorId, Long categoriaId, Long solicitanteId, Long tecnicoId, LocalDate inicio, LocalDate fim) {
        return (r, q, b) -> {
            var p = b.conjunction();
            if (status != null) p = b.and(p, b.equal(r.get("status"), status));
            if (prioridade != null) p = b.and(p, b.equal(r.get("prioridade"), prioridade));
            if (setorId != null) p = b.and(p, b.equal(r.get("categoria").get("setor").get("id"), setorId));
            if (categoriaId != null) p = b.and(p, b.equal(r.get("categoria").get("id"), categoriaId));
            if (solicitanteId != null) p = b.and(p, b.equal(r.get("solicitante").get("id"), solicitanteId));
            if (tecnicoId != null) p = b.and(p, b.equal(r.get("tecnico").get("id"), tecnicoId));
            if (inicio != null) p = b.and(p, b.greaterThanOrEqualTo(r.get("dataAbertura"), inicio.atStartOfDay()));
            if (fim != null) p = b.and(p, b.lessThan(r.get("dataAbertura"), fim.plusDays(1).atStartOfDay()));
            return p;
        };
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResumoResponse> listar(StatusChamado status, Prioridade prioridade, Long setorId, Long categoriaId, Long solicitanteId, Long tecnicoId, LocalDate inicio, LocalDate fim, Pageable pageable) {
        if (inicio != null && fim != null && inicio.isAfter(fim))
            throw new ApiException(HttpStatus.BAD_REQUEST, "dataInicio deve ser anterior ou igual a dataFim.");
        return chamados.findAll(filtros(status, prioridade, setorId, categoriaId, solicitanteId, tecnicoId, inicio, fim), PageSupport.cap(pageable)).map(ChamadoResumoResponse::from);
    }

    @Transactional(readOnly = true)
    public ChamadoResponse obter(Long id) {
        return ChamadoResponse.from(req(chamados.findById(id), "Chamado"));
    }

    public ChamadoResponse abrir(AbrirChamadoRequest r) {
        var solicitante = req(usuarios.findById(r.solicitanteId()), "Usuário solicitante");
        if (solicitante.getTipo() != TipoUsuario.SOLICITANTE)
            throw ApiException.conflito("Apenas usuário SOLICITANTE pode abrir chamado.");
        var categoria = req(categorias.findById(r.categoriaId()), "Categoria");
        var c = new Chamado();
        c.setTitulo(r.titulo().trim());
        c.setDescricao(r.descricao().trim());
        c.setLocal(r.local().trim());
        c.setSolicitante(solicitante);
        c.setCategoria(categoria);
        c.setPrioridade(r.prioridade() == null ? Prioridade.MEDIA : r.prioridade());
        c.setStatus(StatusChamado.ABERTO);
        return ChamadoResponse.from(chamados.save(c));
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResumoResponse> doUsuario(Long usuarioId, StatusChamado status, Pageable p) {
        var u = req(usuarios.findById(usuarioId), "Usuário");
        Specification<Chamado> base = (r, q, b) -> {
            var by = u.getTipo() == TipoUsuario.SOLICITANTE ? b.equal(r.get("solicitante").get("id"), usuarioId) : b.equal(r.get("tecnico").get("id"), usuarioId);
            return status == null ? by : b.and(by, b.equal(r.get("status"), status));
        };
        return chamados.findAll(base, PageSupport.cap(p)).map(ChamadoResumoResponse::from);
    }

    public ChamadoResponse assumir(Long chamadoId, Long tecnicoId) {
        var c = req(chamados.findById(chamadoId), "Chamado");
        var t = req(usuarios.findById(tecnicoId), "Técnico");
        if (t.getTipo() != TipoUsuario.TECNICO) throw ApiException.conflito("O usuário informado não é técnico.");
        if (c.getStatus() != StatusChamado.ABERTO)
            throw ApiException.conflito("Somente chamados ABERTO podem ser assumidos.");
        if (t.getSetor() == null || !t.getSetor().getId().equals(c.getCategoria().getSetor().getId()))
            throw ApiException.conflito("O técnico deve pertencer ao setor responsável pela categoria.");
        c.setTecnico(t);
        c.setStatus(StatusChamado.EM_ATENDIMENTO);
        return ChamadoResponse.from(c);
    }

    public ChamadoResponse resolver(Long chamadoId, ResolverChamadoRequest r) {
        var c = req(chamados.findById(chamadoId), "Chamado");
        var t = req(usuarios.findById(r.tecnicoId()), "Técnico");
        if (c.getStatus() != StatusChamado.EM_ATENDIMENTO)
            throw ApiException.conflito("Somente chamados EM_ATENDIMENTO podem ser resolvidos.");
        if (t.getTipo() != TipoUsuario.TECNICO || c.getTecnico() == null || !c.getTecnico().getId().equals(t.getId()))
            throw ApiException.conflito("Somente o técnico atribuído pode resolver o chamado.");
        c.setSolucao(r.solucao().trim());
        c.setStatus(StatusChamado.RESOLVIDO);
        c.setDataFechamento(java.time.LocalDateTime.now());
        return ChamadoResponse.from(c);
    }

    public ChamadoResponse cancelar(Long chamadoId, Long solicitanteId) {
        var c = req(chamados.findById(chamadoId), "Chamado");
        var solicitante = req(usuarios.findById(solicitanteId), "Solicitante");
        if (c.getStatus() != StatusChamado.ABERTO)
            throw ApiException.conflito("Somente chamados ABERTO podem ser cancelados.");
        if (solicitante.getTipo() != TipoUsuario.SOLICITANTE || !c.getSolicitante().getId().equals(solicitanteId))
            throw ApiException.conflito("Somente o próprio solicitante pode cancelar o chamado.");
        c.setStatus(StatusChamado.CANCELADO);
        c.setDataFechamento(java.time.LocalDateTime.now());
        return ChamadoResponse.from(c);
    }

    public InteracaoResponse interagir(Long chamadoId, InteracaoRequest r) {
        var c = req(chamados.findById(chamadoId), "Chamado");
        var autor = req(usuarios.findById(r.autorId()), "Autor");
        if (c.getStatus() == StatusChamado.RESOLVIDO || c.getStatus() == StatusChamado.CANCELADO)
            throw ApiException.conflito("Chamado encerrado não aceita novas interações.");
        boolean solicitante = autor.getTipo() == TipoUsuario.SOLICITANTE && c.getSolicitante().getId().equals(autor.getId());
        boolean tecnico = autor.getTipo() == TipoUsuario.TECNICO && c.getTecnico() != null && c.getTecnico().getId().equals(autor.getId());
        if (!solicitante && !tecnico)
            throw ApiException.conflito("Somente solicitante e técnico atribuído podem interagir.");
        return InteracaoResponse.from(interacoes.save(new Interacao(r.texto().trim(), c, autor)));
    }
}
