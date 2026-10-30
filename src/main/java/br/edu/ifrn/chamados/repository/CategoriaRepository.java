package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Categoria;
import org.springframework.data.jpa.repository.*;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {
    boolean existsBySetorId(Long id);
}
