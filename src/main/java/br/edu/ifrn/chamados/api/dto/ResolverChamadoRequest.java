package br.edu.ifrn.chamados.api.dto;

import jakarta.validation.constraints.*;

public record ResolverChamadoRequest(@NotNull Long tecnicoId, @NotBlank String solucao) {
}
