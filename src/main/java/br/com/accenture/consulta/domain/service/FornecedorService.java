package br.com.accenture.consulta.domain.service;

import br.com.accenture.consulta.application.fornecedor.FornecedorDto;
import br.com.accenture.consulta.domain.entities.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FornecedorService {
    Optional<Fornecedor> getByCnpjOuCpf(String cpfCnpj);
    List<Fornecedor> getByNome(String nome);
    List<Fornecedor> getByIds(List<String> ids);
    Optional<Fornecedor> getByEmail(String email);
    Optional<Fornecedor> getById(String id);
    Page<FornecedorDto> getAllFornecedores(Pageable empresaPageable);
    Fornecedor save (Fornecedor fornecedor);
    Optional<Fornecedor> updateFornecedor(Fornecedor fornecedor);


    boolean delete(String id);
}
