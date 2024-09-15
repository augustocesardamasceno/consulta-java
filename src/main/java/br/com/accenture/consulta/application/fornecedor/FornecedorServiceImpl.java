package br.com.accenture.consulta.application.fornecedor;

import br.com.accenture.consulta.domain.entities.Empresa;
import br.com.accenture.consulta.domain.entities.Fornecedor;
import br.com.accenture.consulta.domain.exception.DuplicatedTupleException;
import br.com.accenture.consulta.domain.exception.InvalidBirthdayException;
import br.com.accenture.consulta.domain.exception.InvalidCepException;
import br.com.accenture.consulta.domain.service.FornecedorService;
import br.com.accenture.consulta.repository.EmpresaRepository;
import br.com.accenture.consulta.repository.FornecedorRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final EmpresaRepository empresaRepository;


    @Override
    public Optional<Fornecedor> getByCnpjOuCpf(String cpfCnpj) {
        return fornecedorRepository.findByCpfCnpj(cpfCnpj);
    }


    @Override
    public List<Fornecedor> getByNome(String nome) {
        return fornecedorRepository.findByNome(nome);
    }

    public List<Fornecedor> getByIds(List<String> ids) {
        return fornecedorRepository.findAllById(ids);
    }


    @Override
    public Optional<Fornecedor> getByEmail(String email) {
        return fornecedorRepository.findByEmail(email);
    }

    @Override
    public Optional<Fornecedor> getById(String id) {
        return fornecedorRepository.findById(id);
    }

    @Override
    public Page<FornecedorDto> getAllFornecedores(Pageable fornecedorPageable) {
        Page<Fornecedor> fornecedor = fornecedorRepository.findAll(fornecedorPageable);
        return (Page<FornecedorDto>) fornecedor.map(fornecedorMapper::mapToFornecedorDto);
    }

    @Override
    public Fornecedor save(Fornecedor fornecedor) {
        String cep = fornecedor.getCep();
        Optional<Fornecedor> possibleCnpj = fornecedorRepository.findByCpfCnpj(fornecedor.getCpfCnpj());
        Optional<Fornecedor> possibleEmail = fornecedorRepository.findByEmail(fornecedor.getEmail());
        Optional<Fornecedor> possibleRg = fornecedorRepository.findByRg(fornecedor.getRg());

        String uf = getUfFromCep(cep);

        if (!isValidCep(cep)) {
            throw new InvalidCepException("CEP inválido!");
        }

        if (uf.equals("PR") && fornecedor.getCpfCnpj().length() == 11) {
            if (!isOlderThan18Years(fornecedor.getDataNascimento())) {
                throw new InvalidBirthdayException("Pessoa com CPF no PARANÁ deve ser maior de 18 anos.");
            }
        }

        if (possibleCnpj.isPresent()) {
            throw new DuplicatedTupleException("CNPJ/CPF existe.");
        }

        if (possibleRg.isPresent()) {
            throw new DuplicatedTupleException("RG já existe.");
        }

        if (possibleEmail.isPresent()) {
            throw new DuplicatedTupleException("Email already exists.");
        }


        return fornecedorRepository.save(fornecedor);
    }

    public boolean isOlderThan18Years(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears() >= 18;
    }

    public String getUfFromCep(String cep) {
        String viaCepUrl = "http://viacep.com.br/ws/" + cep + "/json";

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(viaCepUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    if (result != null && result.trim().startsWith("{")) {
                        Gson gson = new Gson();
                        JsonReader reader = new JsonReader(new StringReader(result));
                        reader.setLenient(true);

                        JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
                        if (jsonElement.isJsonObject()) {
                            JsonObject json = jsonElement.getAsJsonObject();
                            if (json.has("erro") && json.get("erro").getAsBoolean()) {
                                return null;  // Invalid CEP
                            }
                            // Extract the UF (state) from the response
                            return json.get("uf").getAsString();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
        }
        return null;
    }


    public boolean isValidCep(String cep) {
        if (cep == null || !cep.matches("\\d{5}-?\\d{3}")) {
            System.out.println("Formato de CEP inválido: " + cep);
            return false;
        }
        String viaCepUrl = "http://viacep.com.br/ws/" + cep + "/json";

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(viaCepUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    if (result != null && result.trim().startsWith("{")) {
                        Gson gson = new Gson();
                        JsonReader reader = new JsonReader(new StringReader(result));
                        reader.setLenient(true);
                        JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
                        if (jsonElement.isJsonObject()) {
                            JsonObject json = jsonElement.getAsJsonObject();

                            if (json.has("erro") && json.get("erro").getAsBoolean()) {
                                return false;
                            }

                            return true;
                        } else if (jsonElement.isJsonPrimitive()) {
                            System.out.println("Received unexpected primitive response: " + jsonElement.getAsString());
                            return false;
                        }
                    } else {
                        System.out.println("Received a non-JSON response: " + result);
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
        }

        return false; // Return false if something went wrong
    }




    @Override
    public Optional<Fornecedor> updateFornecedor(Fornecedor fornecedor) {
        var existingFornecedor = fornecedorRepository.findById(fornecedor.getId());

        if (existingFornecedor.isPresent()) {
            Fornecedor fornecedorToUpdate = existingFornecedor.get();

            if (fornecedor.getNome() != null) {
                fornecedorToUpdate.setNome(fornecedor.getNome());
            }
            if (fornecedor.getEmail() != null) {
                fornecedorToUpdate.setEmail(fornecedor.getEmail());
            }
            if (fornecedor.getCep() != null) {
                fornecedorToUpdate.setCep(fornecedor.getCep());
            }
            if (fornecedor.getCpfCnpj() != null) {
                fornecedorToUpdate.setCpfCnpj(fornecedor.getCpfCnpj());
            }

            return Optional.of(fornecedorRepository.save(fornecedorToUpdate));
        }

        return Optional.empty();    }


    @Transactional
    @Override
    public boolean delete(String fornecedorId) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor não encontrado"));

        for (Empresa empresa : fornecedor.getEmpresas()) {
            empresa.getFornecedores().remove(fornecedor);
            empresaRepository.save(empresa);
        }

        fornecedorRepository.delete(fornecedor);
        return true;
    }

}
