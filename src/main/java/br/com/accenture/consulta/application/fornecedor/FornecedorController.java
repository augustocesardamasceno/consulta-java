package br.com.accenture.consulta.application.fornecedor;

import br.com.accenture.consulta.domain.entities.Fornecedor;
import br.com.accenture.consulta.domain.exception.DuplicatedTupleException;
import br.com.accenture.consulta.domain.service.FornecedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8082")
public class FornecedorController {

    private final FornecedorService fornecedorService;
    private final FornecedorMapper fornecedorMapper;

    @GetMapping("/cpfCnpj/{cpfCnpj}")
    public ResponseEntity<FornecedorDto> getByCpfCnpj(@PathVariable String cpfCnpj) {
        Optional<Fornecedor> fornecedor = fornecedorService.getByCnpjOuCpf(cpfCnpj);
        return fornecedor.map(f -> ResponseEntity.ok(fornecedorMapper.mapToFornecedorDto(f)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<FornecedorDto>> getByNome(@PathVariable String nome) {
        List<Fornecedor> fornecedores = fornecedorService.getByNome(nome);
        if (!fornecedores.isEmpty()) {
            return ResponseEntity.ok(
                    fornecedores.stream()
                            .map(fornecedorMapper::mapToFornecedorDto)
                            .collect(Collectors.toList())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



    @GetMapping("/email/{email}")
    public ResponseEntity<FornecedorDto> getByEmail(@PathVariable String email) {
        Optional<Fornecedor> fornecedor = fornecedorService.getByEmail(email);
        return fornecedor.map(f -> ResponseEntity.ok(fornecedorMapper.mapToFornecedorDto(f)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<Page<FornecedorDto>> getAllFornecedores(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable) {
        Page<FornecedorDto> fornecedores = fornecedorService.getAllFornecedores(pageable);
        if (fornecedores.hasContent()) {
            return ResponseEntity.status(HttpStatus.OK).body(fornecedores);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createFornecedor(@RequestBody FornecedorDto fornecedorDto) {
        try{
            String cleanedCpfCnpj = fornecedorDto.getCpfCnpj().replaceAll("[^\\d]", "");
            fornecedorDto.setCpfCnpj(cleanedCpfCnpj);

            Fornecedor fornecedor = fornecedorMapper.mapToFornecedor(fornecedorDto);
            fornecedorService.save(fornecedor);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicatedTupleException e){
            Map<String, String> jsonResultado = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonResultado);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity updateFornecedor(@PathVariable String id, @RequestBody FornecedorDto fornecedorDto) {
        Optional<Fornecedor> existingFornecedor = fornecedorService.getById(id);

        if (existingFornecedor.isPresent()) {
            String cleanedCpfCnpj = fornecedorDto.getCpfCnpj().replaceAll("[^\\d]", "");
            fornecedorDto.setCpfCnpj(cleanedCpfCnpj);

                Fornecedor fornecedorToUpdate = fornecedorMapper.mapToFornecedor(fornecedorDto);
            fornecedorToUpdate.setId(id);
            fornecedorService.updateFornecedor(fornecedorToUpdate);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFornecedor(@PathVariable String id) {
        boolean deleted = fornecedorService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
