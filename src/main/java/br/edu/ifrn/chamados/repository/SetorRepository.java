package br.edu.ifrn.chamados.repository;

import br.edu.ifrn.chamados.model.Setor;
import org.springframework.data.jpa.repository.*;

public interface SetorRepository extends JpaRepository<Setor, Long>, JpaSpecificationExecutor<Setor> {
    boolean existsBySiglaIgnoreCase(String sigla);
}
