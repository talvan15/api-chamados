package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.Categoria;

public record CategoriaResponse(Long id, String nome, String descricao, Long setorId, String setorNome) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao(), c.getSetor().getId(), c.getSetor().getNome());
    }
}
