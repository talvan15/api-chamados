package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.Prioridade;
import jakarta.validation.constraints.*;

public record AbrirChamadoRequest(@NotBlank String titulo, @NotBlank String descricao, @NotBlank String local,
                                  @NotNull Long categoriaId, @NotNull Long solicitanteId, Prioridade prioridade) {
}
