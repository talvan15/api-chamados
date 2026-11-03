package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.TipoUsuario;
import jakarta.validation.constraints.*;

public record UsuarioRequest(@NotBlank String nome, @Email @NotBlank String email, @NotBlank String matricula,
                             @NotNull TipoUsuario tipo, Long setorId) {
}
