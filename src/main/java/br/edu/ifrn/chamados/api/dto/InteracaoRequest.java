package br.edu.ifrn.chamados.api.dto;

import jakarta.validation.constraints.*;

public record InteracaoRequest(@NotNull Long autorId, @NotBlank String texto) {
}
