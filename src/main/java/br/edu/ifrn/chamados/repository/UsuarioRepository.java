package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Usuario;
import org.springframework.data.jpa.repository.*;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMatriculaIgnoreCase(String matricula);

    boolean existsBySetorId(Long setorId);
}
