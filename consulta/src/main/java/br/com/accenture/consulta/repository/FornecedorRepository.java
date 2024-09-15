package br.com.accenture.consulta.repository;

import br.com.accenture.consulta.domain.entities.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, String>, JpaSpecificationExecutor<Fornecedor> {
    Optional<Fornecedor> findById(String id);
    Optional<Fornecedor> findByEmail(String email);
    Optional<Fornecedor> findByCpfCnpj(String cpfCnpj);
    @Query("SELECT f FROM Fornecedor f WHERE LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Fornecedor> findByNome(@Param("nome") String nome);

    Optional<Fornecedor> findByRg(String rg);
}
