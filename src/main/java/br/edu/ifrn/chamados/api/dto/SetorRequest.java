package br.edu.ifrn.chamados.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SetorRequest(@NotBlank String nome, @NotBlank String sigla) {
}
