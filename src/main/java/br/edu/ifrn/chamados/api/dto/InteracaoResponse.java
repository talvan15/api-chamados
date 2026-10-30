package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.Interacao;

import java.time.LocalDateTime;

public record InteracaoResponse(Long id, String texto, LocalDateTime dataHora, Long autorId, String autorNome) {
    public static InteracaoResponse from(Interacao i) {
        return new InteracaoResponse(i.getId(), i.getTexto(), i.getDataHora(), i.getAutor().getId(), i.getAutor().getNome());
    }
}
