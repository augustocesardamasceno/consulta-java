package br.com.accenture.consulta.application.empresa;


import br.com.accenture.consulta.application.fornecedor.FornecedorDto;
import br.com.accenture.consulta.application.fornecedor.FornecedorMapper;
import br.com.accenture.consulta.application.fornecedor.FornecedorServiceImpl;
import br.com.accenture.consulta.domain.entities.Empresa;
import br.com.accenture.consulta.domain.entities.Fornecedor;
import br.com.accenture.consulta.domain.exception.DuplicatedTupleException;
import br.com.accenture.consulta.domain.service.EmpresaService;
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
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8082")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EmpresaMapper empresaMapper;
    private final FornecedorService fornecedorService;
    private final FornecedorMapper fornecedorMapper;

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<EmpresaDto> getByCnpj(@PathVariable String cnpj) {
        Optional<Empresa> empresa = empresaService.getByCnpj(cnpj);
        return empresa.map(e -> ResponseEntity.ok(empresaMapper.mapToEmpresaDto(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nomeFantasia/{nomeFantasia}")
    public ResponseEntity<List<EmpresaDto>> getByNomeFantasia(@PathVariable String nomeFantasia) {
       List <Empresa> empresa = empresaService.getByNomeFantasia(nomeFantasia);
        if (!empresa.isEmpty()) {
            return ResponseEntity.ok(
                     empresa.stream()
                            .map(empresaMapper::mapToEmpresaDto)
                            .collect(Collectors.toList())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping
    public ResponseEntity<Page<EmpresaDto>> getAllEmpresas(@PageableDefault(size = 10, sort = {"nomeFantasia"}) Pageable pageable) {
        var page = empresaService.getAllEmpresas(pageable);
        if (page.hasContent()){
            return ResponseEntity.status(HttpStatus.OK).body(page);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createEmpresa(@RequestBody EmpresaDto empresaDto) {
        try {
            String cleanedCnpj = empresaDto.getCnpj().replaceAll("[^\\d]", "");  // Mantém apenas os números
            empresaDto.setCnpj(cleanedCnpj);
            Empresa empresa = empresaMapper.mapToEmpresa(empresaDto);  // Map here
            empresaService.save(empresa);  // Pass entity
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicatedTupleException e) {
            Map<String, String> jsonResultado = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonResultado);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable String id, @RequestBody EmpresaDto dto){
        try {
            Optional<Empresa> existingUser = empresaService.getById(id);

            if (existingUser.isPresent()) {
                Empresa updatedEmpresa = empresaMapper.mapToEmpresa(dto);
                updatedEmpresa.setId(id);
                empresaService.updateEmpresa(updatedEmpresa);

                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (DuplicatedTupleException e){
            Map<String, String> jsonResultado = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonResultado);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable String id) {
        boolean deleted = empresaService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/vincular-fornecedor/{id}")
    public ResponseEntity<String> vincularFornecedores(
            @PathVariable String id,
            @RequestBody List<String> fornecedoresIds) {

        Optional<Empresa> empresaOpt = empresaService.getById(id);

        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();

            List<Fornecedor> novosFornecedores = fornecedorService.getByIds(fornecedoresIds);

            List<Fornecedor> fornecedoresAtuais = empresa.getFornecedores();

            for (Fornecedor fornecedor : novosFornecedores) {
                if (!fornecedoresAtuais.contains(fornecedor)) {
                    fornecedoresAtuais.add(fornecedor);
                }
            }

            empresa.setFornecedores(fornecedoresAtuais);

            empresaService.save(empresa);

            return ResponseEntity.ok("Fornecedores vinculados com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa não encontrada.");
        }
    }




    @GetMapping("/cnpj/{cnpj}/fornecedores")
    public ResponseEntity<List<FornecedorDto>> getFornecedoresByCnpj(@PathVariable String cnpj) {
        Optional<Empresa> empresaOpt = empresaService.getByCnpj(cnpj);

        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            List<FornecedorDto> fornecedorDtos = empresa.getFornecedores().stream()
                    .map(fornecedorMapper::mapToFornecedorDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fornecedorDtos);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/desvincular-fornecedor/{id}")
    public ResponseEntity<String> desvincularFornecedores(
            @PathVariable String id,
            @RequestBody List<String> fornecedoresIds) {

        Optional<Empresa> empresaOpt = empresaService.getById(id);

        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();

            List<Fornecedor> fornecedoresAtualizados = empresa.getFornecedores()
                    .stream()
                    .filter(fornecedor -> !fornecedoresIds.contains(fornecedor.getId()))
                    .collect(Collectors.toList());

            empresa.setFornecedores(fornecedoresAtualizados);
            empresaService.save(empresa);  // Salva a empresa com os fornecedores atualizados

            return ResponseEntity.ok("Fornecedores desvinculados com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa não encontrada.");
        }
    }



}

