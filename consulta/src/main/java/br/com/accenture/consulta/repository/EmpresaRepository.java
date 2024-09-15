package br.com.accenture.consulta.repository;

import br.com.accenture.consulta.application.empresa.EmpresaDto;
import br.com.accenture.consulta.domain.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, String>, JpaSpecificationExecutor<Empresa> {
    Optional<Empresa> findByCnpj(String cnpj);
    Optional<Empresa> findById(String id);
    @Query("SELECT e FROM Empresa e WHERE LOWER(e.nomeFantasia) LIKE LOWER(CONCAT('%', :nomeFantasia, '%'))")
    List<Empresa> findByNomeFantasia(@Param("nomeFantasia") String nomeFantasia);
}
