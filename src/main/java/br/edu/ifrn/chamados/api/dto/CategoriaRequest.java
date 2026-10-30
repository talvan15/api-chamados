package br.edu.ifrn.chamados.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(@NotBlank String nome, String descricao, @NotNull Long setorId) {
}
