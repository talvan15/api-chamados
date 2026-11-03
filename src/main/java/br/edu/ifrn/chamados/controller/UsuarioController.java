package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.api.*;
import br.edu.ifrn.chamados.api.dto.*;
import br.edu.ifrn.chamados.model.TipoUsuario;
import br.edu.ifrn.chamados.service.*;
import br.edu.ifrn.chamados.model.StatusChamado;
import org.springframework.data.web.PageableDefault;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final CadastroService service;
    private final ChamadoService chamados;

    public UsuarioController(CadastroService s, ChamadoService c) {
        service = s;
        chamados = c;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest r) {
        return ResponseEntity.status(201).body(service.criarUsuario(r));
    }

    @GetMapping
    public PageResponse<UsuarioResponse> listar(@RequestParam(required = false) String nome, @RequestParam(required = false) TipoUsuario tipo, @RequestParam(required = false) Long setorId, @PageableDefault(size = 10, sort = "nome") Pageable p) {
        return PageResponse.from(service.listarUsuarios(nome, tipo, setorId, p), x -> x);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obter(@PathVariable Long id) {
        return service.usuario(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest r) {
        return service.atualizarUsuario(id, r);
    }

    @GetMapping("/{id}/chamados")
    public PageResponse<ChamadoResumoResponse> chamados(@PathVariable Long id, @RequestParam(required = false) StatusChamado status, @PageableDefault(size = 10, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable p) {
        return PageResponse.from(chamados.doUsuario(id, status, p), x -> x);
    }
}
