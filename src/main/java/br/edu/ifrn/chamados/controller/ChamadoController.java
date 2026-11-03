package br.edu.ifrn.chamados.controller;

import br.edu.ifrn.chamados.api.*;
import br.edu.ifrn.chamados.api.dto.*;
import br.edu.ifrn.chamados.model.*;
import br.edu.ifrn.chamados.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {
    private final ChamadoService service;

    public ChamadoController(ChamadoService s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> abrir(@Valid @RequestBody AbrirChamadoRequest r) {
        return ResponseEntity.status(201).body(service.abrir(r));
    }

    @GetMapping
    public PageResponse<ChamadoResumoResponse> listar(@RequestParam(required = false) StatusChamado status, @RequestParam(required = false) Prioridade prioridade, @RequestParam(required = false) Long setorId, @RequestParam(required = false) Long categoriaId, @RequestParam(required = false) Long solicitanteId, @RequestParam(required = false) Long tecnicoId, @RequestParam(required = false) LocalDate dataInicio, @RequestParam(required = false) LocalDate dataFim, @PageableDefault(size = 10, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable p) {
        return PageResponse.from(service.listar(status, prioridade, setorId, categoriaId, solicitanteId, tecnicoId, dataInicio, dataFim, p), x -> x);
    }

    @GetMapping("/{id}")
    public ChamadoResponse obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PatchMapping("/{id}/assumir")
    public ChamadoResponse assumir(@PathVariable Long id, @Valid @RequestBody AssumirChamadoRequest r) {
        return service.assumir(id, r.tecnicoId());
    }

    @PatchMapping("/{id}/resolver")
    public ChamadoResponse resolver(@PathVariable Long id, @Valid @RequestBody ResolverChamadoRequest r) {
        return service.resolver(id, r);
    }

    @PatchMapping("/{id}/cancelar")
    public ChamadoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarChamadoRequest r) {
        return service.cancelar(id, r.solicitanteId());
    }

    @PostMapping("/{id}/interacoes")
    public ResponseEntity<InteracaoResponse> interacao(@PathVariable Long id, @Valid @RequestBody InteracaoRequest r) {
        return ResponseEntity.status(201).body(service.interagir(id, r));
    }
}
