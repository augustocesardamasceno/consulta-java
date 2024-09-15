package br.com.accenture.consulta.application.empresa;

import br.com.accenture.consulta.application.fornecedor.FornecedorDto;
import br.com.accenture.consulta.application.fornecedor.FornecedorMapper;
import br.com.accenture.consulta.domain.entities.Empresa;
import br.com.accenture.consulta.domain.entities.Fornecedor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmpresaMapper {

    private final FornecedorMapper fornecedorMapper;

    public EmpresaMapper(FornecedorMapper fornecedorMapper) {
        this.fornecedorMapper = fornecedorMapper;
    }

    public Empresa mapToEmpresa(EmpresaDto empresaDto) {
        List<Fornecedor> fornecedores = empresaDto.getFornecedorDtos() != null
                ? empresaDto.getFornecedorDtos().stream()
                .map(fornecedorMapper::mapToFornecedor)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return Empresa.builder()
                .nomeFantasia(empresaDto.getNomeFantasia())
                .cep(empresaDto.getCep())
                .cnpj(empresaDto.getCnpj())
                .fornecedores(fornecedores)
                .build();
    }


    public EmpresaDto mapToEmpresaDto(Empresa empresa) {
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
    }
}
