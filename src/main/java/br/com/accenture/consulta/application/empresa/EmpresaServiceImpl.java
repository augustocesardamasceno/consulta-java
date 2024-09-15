package br.com.accenture.consulta.application.empresa;

import br.com.accenture.consulta.application.fornecedor.FornecedorDto;
import br.com.accenture.consulta.application.fornecedor.FornecedorMapper;
import br.com.accenture.consulta.application.fornecedor.FornecedorServiceImpl;
import br.com.accenture.consulta.domain.entities.Empresa;
import br.com.accenture.consulta.domain.entities.Fornecedor;
import br.com.accenture.consulta.domain.service.EmpresaService;
import br.com.accenture.consulta.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepo;
    private final EmpresaMapper empresaMapper;
    private final FornecedorMapper fornecedorMapper;
    private final FornecedorServiceImpl fornecedorService;

    @Override
    public Optional<Empresa> getByCnpj(String cnpj) {
        return empresaRepo.findByCnpj(cnpj);
    }

    @Override
    public List<Empresa> getByNomeFantasia(String nomeFantasia) {
        return empresaRepo.findByNomeFantasia(nomeFantasia);
    }

    @Override
    public Optional<Empresa> getById(String id) {
        return empresaRepo.findById(id);
    }

    @Override
    public Page<EmpresaDto> getAllEmpresas(Pageable empresaPageable) {
        Page<Empresa> empresas = empresaRepo.findAll(empresaPageable);

        return empresas.map(empresa -> {
            List<FornecedorDto> fornecedorDtos = empresa.getFornecedores().stream()
                    .map(fornecedorMapper::mapToFornecedorDto)
                    .collect(Collectors.toList());

            return new EmpresaDto(
                    empresa.getId(),
                    empresa.getCnpj(),
                    empresa.getNomeFantasia(),
                    empresa.getCep(),
                    fornecedorDtos
            );
        });
    }


    @Transactional
    @Override
    public Empresa save(Empresa empresa) {
        return empresaRepo.save(empresa);
    }


    @Override
    @Transactional
    public Optional<Empresa> updateEmpresa(Empresa empresa) {
        var existingEmpresaOpt = empresaRepo.findById(empresa.getId());

        if (existingEmpresaOpt.isPresent()) {
            Empresa empresaToUpdate = existingEmpresaOpt.get();

            // Atualizar os dados básicos (nome fantasia, CNPJ, CEP)
            if (empresa.getNomeFantasia() != null) {
                empresaToUpdate.setNomeFantasia(empresa.getNomeFantasia());
            }
            if (empresa.getCnpj() != null) {
                empresaToUpdate.setCnpj(empresa.getCnpj());
            }
            if (empresa.getCep() != null) {
                empresaToUpdate.setCep(empresa.getCep());
            }

            if (empresa.getFornecedores() != null) {
                List<Fornecedor> fornecedoresExistentes = empresaToUpdate.getFornecedores();
                List<Fornecedor> novosFornecedores = empresa.getFornecedores();

                for (Fornecedor novoFornecedor : novosFornecedores) {
                    if (!fornecedoresExistentes.contains(novoFornecedor)) {
                        fornecedoresExistentes.add(novoFornecedor);
                    }
                }

                empresaToUpdate.setFornecedores(fornecedoresExistentes);
            }

            return Optional.of(empresaRepo.save(empresaToUpdate));
        }

        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        if (empresaRepo.existsById(id)) {
            empresaRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
