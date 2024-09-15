package br.com.accenture.consulta.domain.service;

import br.com.accenture.consulta.application.empresa.EmpresaDto;
import br.com.accenture.consulta.domain.entities.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmpresaService {

    Optional <Empresa> getByCnpj(String cnpj);
    List<Empresa> getByNomeFantasia(String nomeFantasia);
    Optional<Empresa> getById(String id);
    Page<EmpresaDto> getAllEmpresas(Pageable empresaPageable);
    Empresa save(Empresa empresa);
    Optional<Empresa> updateEmpresa(Empresa empresa);

    boolean delete(String id);

}
