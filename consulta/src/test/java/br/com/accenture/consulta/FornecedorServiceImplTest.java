package br.com.accenture.consulta;

import br.com.accenture.consulta.application.fornecedor.FornecedorDto;
import br.com.accenture.consulta.application.fornecedor.FornecedorMapper;
import br.com.accenture.consulta.application.fornecedor.FornecedorServiceImpl;
import br.com.accenture.consulta.domain.entities.Fornecedor;
import br.com.accenture.consulta.domain.exception.DuplicatedTupleException;
import br.com.accenture.consulta.domain.exception.InvalidBirthdayException;
import br.com.accenture.consulta.domain.exception.InvalidCepException;
import br.com.accenture.consulta.repository.EmpresaRepository;
import br.com.accenture.consulta.repository.FornecedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FornecedorServiceImplTest {

    @InjectMocks
    private FornecedorServiceImpl fornecedorService;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnFornecedorWhenGetByCnpjOuCpf() {
        String cpfCnpj = "12345678901";
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCpfCnpj(cpfCnpj);

        when(fornecedorRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(fornecedor));

        Optional<Fornecedor> result = fornecedorService.getByCnpjOuCpf(cpfCnpj);

        assertTrue(result.isPresent());
        assertEquals(cpfCnpj, result.get().getCpfCnpj());
    }

    @Test
    void shouldReturnFornecedorListWhenGetByNome() {
        String nome = "Teste";
        Fornecedor fornecedor1 = new Fornecedor();
        fornecedor1.setNome(nome);
        Fornecedor fornecedor2 = new Fornecedor();
        fornecedor2.setNome(nome);

        when(fornecedorRepository.findByNome(nome)).thenReturn(Arrays.asList(fornecedor1, fornecedor2));

        List<Fornecedor> result = fornecedorService.getByNome(nome);

        assertEquals(2, result.size());
        assertEquals(nome, result.get(0).getNome());
    }

    @Test
    void shouldThrowDuplicatedTupleExceptionWhenSaveWithDuplicateCpfCnpj() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCpfCnpj("12345678901");
        fornecedor.setCep("12345-678"); // Definir um CEP válido para não interferir
        fornecedor.setNome("Fornecedor Teste");
        fornecedor.setDataNascimento(LocalDate.now().minusYears(20));

        FornecedorServiceImpl fornecedorSpy = spy(fornecedorService);

        doReturn("SP").when(fornecedorSpy).getUfFromCep(fornecedor.getCep());

        doReturn(true).when(fornecedorSpy).isValidCep(fornecedor.getCep());

        when(fornecedorRepository.findByCpfCnpj(fornecedor.getCpfCnpj())).thenReturn(Optional.of(fornecedor));

        assertThrows(DuplicatedTupleException.class, () -> fornecedorSpy.save(fornecedor));
    }



    @Test
    void shouldThrowInvalidCepExceptionWhenSaveWithInvalidCep() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCep("12345-678");

        // O fornecedorService deve ser um spy para que possamos mockar seus métodos
        FornecedorServiceImpl fornecedorSpy = spy(fornecedorService);

        // Mockando o método isValidCep
        doReturn(false).when(fornecedorSpy).isValidCep(fornecedor.getCep());

        assertThrows(InvalidCepException.class, () -> fornecedorSpy.save(fornecedor));
    }

    @Test
    void shouldThrowInvalidBirthdayExceptionWhenPersonIsYoungerThan18() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCep("80000-000"); // CEP válido para o Paraná
        fornecedor.setCpfCnpj("12345678901"); // CPF com 11 dígitos (indica pessoa física)
        fornecedor.setDataNascimento(LocalDate.now().minusYears(17)); // Menor de 18 anos

        // O fornecedorService deve ser um spy para que possamos mockar seus métodos
        FornecedorServiceImpl fornecedorSpy = spy(fornecedorService);

        // Mockando os métodos getUfFromCep e isValidCep
        doReturn("PR").when(fornecedorSpy).getUfFromCep(fornecedor.getCep());
        doReturn(true).when(fornecedorSpy).isValidCep(fornecedor.getCep());

        assertThrows(InvalidBirthdayException.class, () -> fornecedorSpy.save(fornecedor));
    }


    @Test
    void shouldReturnFornecedorWhenGetById() {
        String id = "1";
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(id);

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(fornecedor));

        Optional<Fornecedor> result = fornecedorService.getById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void shouldReturnPageOfFornecedoresWhenGetAllFornecedores() {
        Pageable pageable = PageRequest.of(0, 10);
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Fornecedor Teste");
        Page<Fornecedor> page = new PageImpl<>(Arrays.asList(fornecedor));

        when(fornecedorRepository.findAll(pageable)).thenReturn(page);
        when(fornecedorMapper.mapToFornecedorDto(any())).thenReturn(new FornecedorDto());

        Page<FornecedorDto> result = fornecedorService.getAllFornecedores(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldDeleteFornecedor() {
        String id = "1";
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(id);
        fornecedor.setEmpresas(new ArrayList<>()); // Inicializando a lista de empresas

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(fornecedor));

        fornecedorService.delete(id);

        verify(fornecedorRepository, times(1)).delete(fornecedor);
    }


    @Test
    void shouldUpdateFornecedor() {
        String id = "1";
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(id);
        fornecedor.setNome("Novo Nome");

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedor);

        Optional<Fornecedor> result = fornecedorService.updateFornecedor(fornecedor);

        assertTrue(result.isPresent());
        assertEquals("Novo Nome", result.get().getNome());
    }

    @Test
    void shouldReturnTrueIfOlderThan18Years() {
        LocalDate birthDate = LocalDate.now().minusYears(20);
        boolean isOlder = fornecedorService.isOlderThan18Years(birthDate);
        assertTrue(isOlder);
    }

    @Test
    void shouldReturnFalseIfYoungerThan18Years() {
        LocalDate birthDate = LocalDate.now().minusYears(17);
        boolean isOlder = fornecedorService.isOlderThan18Years(birthDate);
        assertFalse(isOlder);
    }
}

