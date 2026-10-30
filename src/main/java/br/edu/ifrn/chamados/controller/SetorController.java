package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.api.*;
import br.edu.ifrn.chamados.api.dto.*;
import br.edu.ifrn.chamados.service.CadastroService;
import br.edu.ifrn.chamados.model.Categoria;
import org.springframework.data.web.PageableDefault;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setores")
public class SetorController {
    private final CadastroService service;

    public SetorController(CadastroService s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<SetorResponse> criar(@Valid @RequestBody SetorRequest r) {
        return ResponseEntity.status(201).body(service.criarSetor(r));
    }

    @GetMapping
    public PageResponse<SetorResponse> listar(@RequestParam(required = false) String nome, @PageableDefault(size = 10, sort = "nome") Pageable p) {
        return PageResponse.from(service.listarSetores(nome, p), x -> x);
    }

    @GetMapping("/{id}")
    public SetorResponse obter(@PathVariable Long id) {
        return service.setor(id);
    }

    @GetMapping("/{id}/categorias")
    public PageResponse<CategoriaResponse> categorias(@PathVariable Long id, @PageableDefault(size = 10, sort = "nome") Pageable p) {
        return PageResponse.from(service.categoriasDoSetor(id, p), x -> x);
    }

    @PutMapping("/{id}")
    public SetorResponse atualizar(@PathVariable Long id, @Valid @RequestBody SetorRequest r) {
        return service.atualizarSetor(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirSetor(id);
        return ResponseEntity.noContent().build();
    }
}
