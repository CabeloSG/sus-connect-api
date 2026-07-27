package br.com.susconnect.application.patient.usecase;

import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.patient.application.dto.CreatePatientRequest;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.application.usecase.CreatePatientUseCase;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.domain.enums.PatientStatus;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pelo cadastro de pacientes.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class CreatePatientUseCaseTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private PatientMapper mapper;

    @InjectMocks
    private CreatePatientUseCase useCase;

    private CreatePatientRequest request;
    private Patient patient;
    private PatientResponse response;

    @BeforeEach
    void setUp() {

        request = CreatePatientRequest.builder()
                .fullName("João da Silva")
                .cpf("12345678901")
                .birthDate(LocalDate.of(1995, 8, 12))
                .phone("67999998888")
                .email("joao@email.com")
                .susCard("706123456789000")
                .build();

        patient = Patient.builder()
                .fullName(request.getFullName())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .email(request.getEmail())
                .susCard(request.getSusCard())
                .status(PatientStatus.ACTIVE)
                .build();

        response = PatientResponse.builder()
                .fullName(patient.getFullName())
                .cpf(patient.getCpf())
                .birthDate(patient.getBirthDate())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .susCard(patient.getSusCard())
                .status(patient.getStatus())
                .build();
    }

    @Test
    void shouldCreatePatientSuccessfully() {

        when(repository.existsByCpf(request.getCpf()))
                .thenReturn(false);

        when(repository.existsBySusCard(request.getSusCard()))
                .thenReturn(false);

        when(mapper.toEntity(request))
                .thenReturn(patient);

        when(repository.save(patient))
                .thenReturn(patient);

        when(mapper.toResponse(patient))
                .thenReturn(response);

        PatientResponse result = useCase.execute(request);

        assertEquals("João da Silva", result.getFullName());
        assertEquals("12345678901", result.getCpf());
        assertEquals("706123456789000", result.getSusCard());
        assertEquals(PatientStatus.ACTIVE, result.getStatus());

        verify(repository).existsByCpf(request.getCpf());
        verify(repository).existsBySusCard(request.getSusCard());
        verify(mapper).toEntity(request);
        verify(repository).save(patient);
        verify(mapper).toResponse(patient);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {

        when(repository.existsByCpf(request.getCpf()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> useCase.execute(request)
        );

        assertEquals(
                "Já existe um paciente cadastrado com este CPF.",
                exception.getMessage()
        );

        verify(repository).existsByCpf(request.getCpf());

        verify(repository, never())
                .existsBySusCard(any());

        verify(repository, never())
                .save(any());

        verify(mapper, never())
                .toEntity(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenSusCardAlreadyExists() {

        when(repository.existsByCpf(request.getCpf()))
                .thenReturn(false);

        when(repository.existsBySusCard(request.getSusCard()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> useCase.execute(request)
        );

        assertEquals(
                "Já existe um paciente cadastrado com este Cartão SUS.",
                exception.getMessage()
        );

        verify(repository).existsByCpf(request.getCpf());
        verify(repository).existsBySusCard(request.getSusCard());

        verify(repository, never())
                .save(any());

        verify(mapper, never())
                .toEntity(any());
    }
}