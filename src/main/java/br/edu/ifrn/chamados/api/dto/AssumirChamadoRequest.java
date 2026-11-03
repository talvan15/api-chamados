package br.edu.ifrn.chamados.api.dto;

import jakarta.validation.constraints.NotNull;

public record AssumirChamadoRequest(@NotNull Long tecnicoId) {
}
