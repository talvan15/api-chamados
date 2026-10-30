package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.Setor;

public record SetorResponse(Long id, String nome, String sigla) {
    public static SetorResponse from(Setor s) {
        return new SetorResponse(s.getId(), s.getNome(), s.getSigla());
    }
}
