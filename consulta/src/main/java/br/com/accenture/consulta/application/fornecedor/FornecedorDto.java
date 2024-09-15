package br.com.accenture.consulta.application.fornecedor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FornecedorDto {
    private String id;
    private String cpfCnpj;
    private String nome;
    private String email;
    private String cep;
    private String rg;
    private LocalDate dataNascimento;
}
