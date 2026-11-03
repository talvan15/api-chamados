package br.edu.ifrn.chamados.controller;
import br.edu.ifrn.chamados.api.*; import br.edu.ifrn.chamados.api.dto.*; import br.edu.ifrn.chamados.service.CadastroService;
import jakarta.validation.Valid; import org.springframework.data.domain.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;
@RestController @RequestMapping("/categorias")
public class CategoriaController {
 private final CadastroService service;

 public CategoriaController(CadastroService s){service=s;}

 @PostMapping
 public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest r) {
  return ResponseEntity.status(201).body(service.criarCategoria(r));
 }

 @GetMapping
 public PageResponse<CategoriaResponse> listar(@RequestParam(required=false) String nome,@RequestParam(required=false) Long setorId,@PageableDefault(size=10,sort="nome") Pageable p){
  return PageResponse.from(service.listarCategorias(nome,setorId,p),x->x);
 }
 @GetMapping("/{id}")
 public CategoriaResponse obter(@PathVariable Long id){
  return service.categoria(id);
 }
 @PutMapping("/{id}")
 public CategoriaResponse atualizar(@PathVariable Long id,@Valid @RequestBody CategoriaRequest r){
  return service.atualizarCategoria(id,r);
 }

 @DeleteMapping("/{id}")
 public ResponseEntity<Void> excluir(@PathVariable Long id){
  service.excluirCategoria(id);return ResponseEntity.noContent().build();
 }
}
