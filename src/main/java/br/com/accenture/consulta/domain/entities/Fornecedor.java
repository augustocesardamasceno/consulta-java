package br.com.accenture.consulta.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "fornecedor_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String cpfCnpj;
    @Column
    private String nome;
    @Column
    @Email
    private String email;
    @Column
    private String cep;
    @Column(unique = true)
    private String rg;
    @Column
    private LocalDate dataNascimento;
    @ManyToMany(mappedBy = "fornecedores")
    private List<Empresa> empresas;

    public boolean isPessoaFisica() {
        return this.cpfCnpj != null && this.cpfCnpj.length() == 11;  // Assuming CPF is 11 digits long
    }



}
