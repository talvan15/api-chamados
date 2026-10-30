package br.edu.ifrn.chamados.api.dto;

import br.edu.ifrn.chamados.model.*;

public record UsuarioResponse(Long id, String nome, String email, String matricula, TipoUsuario tipo, Long setorId,
                              String setorNome) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getMatricula(), u.getTipo(), u.getSetor() == null ? null : u.getSetor().getId(), u.getSetor() == null ? null : u.getSetor().getNome());
    }
}
