package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.*;

import java.time.LocalDateTime;

public record ChamadoResumoResponse(Long id, String titulo, String descricao, String local, Prioridade prioridade,
                                    StatusChamado status, LocalDateTime dataAbertura, LocalDateTime dataFechamento,
                                    String solucao, Long solicitanteId, String solicitanteNome, Long categoriaId,
                                    String categoriaNome, Long setorId, String setorNome, Long tecnicoId,
                                    String tecnicoNome) {
    public static ChamadoResumoResponse from(Chamado c) {
        return new ChamadoResumoResponse(c.getId(), c.getTitulo(), c.getDescricao(), c.getLocal(), c.getPrioridade(), c.getStatus(), c.getDataAbertura(), c.getDataFechamento(), c.getSolucao(), c.getSolicitante().getId(), c.getSolicitante().getNome(), c.getCategoria().getId(), c.getCategoria().getNome(), c.getCategoria().getSetor().getId(), c.getCategoria().getSetor().getNome(), c.getTecnico() == null ? null : c.getTecnico().getId(), c.getTecnico() == null ? null : c.getTecnico().getNome());
    }
}
