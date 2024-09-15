package br.com.accenture.consulta.application.empresa;

import br.com.accenture.consulta.application.fornecedor.FornecedorDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class EmpresaDto {
    private String id;   // Inclua o ID da empresa
    private String cnpj;
    private String nomeFantasia;
    private String cep;
    private List<FornecedorDto> fornecedorDtos;

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj.replaceAll("[^\\d]", "");
    }

    public EmpresaDto(String id,String cnpj, String nomeFantasia, String cep, List<FornecedorDto> fornecedorDtos) {
        this.id = id;
        this.cnpj = cnpj;
        this.nomeFantasia = nomeFantasia;
        this.cep = cep;
        this.fornecedorDtos = fornecedorDtos;
    }


}
