package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Chamado;
import org.springframework.data.jpa.repository.*;

public interface ChamadoRepository extends JpaRepository<Chamado, Long>, JpaSpecificationExecutor<Chamado> {
    boolean existsByCategoriaId(Long id);
}
