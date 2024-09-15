package br.com.accenture.consulta.application.fornecedor;

import br.com.accenture.consulta.domain.entities.Fornecedor;
import org.springframework.stereotype.Component;


@Component
public class FornecedorMapper {

    public Fornecedor mapToFornecedor(FornecedorDto dto) {
        return Fornecedor.builder()
                .cpfCnpj(dto.getCpfCnpj())
                .nome(dto.getNome())
                .email(dto.getEmail())
                .cep(dto.getCep())
                .rg(dto.getRg())
                .dataNascimento(dto.getDataNascimento())
                .build();
    }

    public FornecedorDto mapToFornecedorDto(Fornecedor fornecedor) {
        return new FornecedorDto(
                fornecedor.getId(),
                fornecedor.getCpfCnpj(),
                fornecedor.getNome(),
                fornecedor.getEmail(),
                fornecedor.getCep(),
                fornecedor.getRg(),
                fornecedor.getDataNascimento()
        );
    }

    public Fornecedor mapToFornecedor(Fornecedor fornecedor) {
        return Fornecedor.builder()
                .cpfCnpj(fornecedor.getCpfCnpj())
                .nome(fornecedor.getNome())
                .email(fornecedor.getEmail())
                .cep(fornecedor.getCep())
                .build();
    }
}

