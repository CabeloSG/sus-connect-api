package br.com.susconnect.application.patient.usecase;

import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.application.usecase.FindPatientByIdUseCase;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela busca de paciente por ID.
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
class FindPatientByIdUseCaseTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private PatientMapper mapper;

    @InjectMocks
    private FindPatientByIdUseCase useCase;

    private UUID patientId;
    private Patient patient;
    private PatientResponse response;

    @BeforeEach
    void setUp() {

        patientId = UUID.randomUUID();

        patient = Patient.builder()
                .fullName("João da Silva")
                .cpf("12345678901")
                .birthDate(LocalDate.of(1995, 8, 12))
                .phone("67999998888")
                .email("joao@email.com")
                .susCard("706123456789000")
                .status(PatientStatus.ACTIVE)
                .build();

        response = PatientResponse.builder()
                .id(patientId)
                .fullName("João da Silva")
                .cpf("12345678901")
                .birthDate(LocalDate.of(1995, 8, 12))
                .phone("67999998888")
                .email("joao@email.com")
                .susCard("706123456789000")
                .status(PatientStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldFindPatientByIdSuccessfully() {

        when(repository.findById(patientId))
                .thenReturn(Optional.of(patient));

        when(mapper.toResponse(patient))
                .thenReturn(response);

        PatientResponse result = useCase.execute(patientId);

        assertEquals(patientId, result.getId());
        assertEquals("João da Silva", result.getFullName());
        assertEquals("12345678901", result.getCpf());
        assertEquals("706123456789000", result.getSusCard());
        assertEquals(PatientStatus.ACTIVE, result.getStatus());

        verify(repository).findById(patientId);
        verify(mapper).toResponse(patient);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist() {

        when(repository.findById(patientId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(patientId)
        );

        assertEquals(
                "Paciente não encontrado.",
                exception.getMessage()
        );

        verify(repository).findById(patientId);
        verify(mapper, never()).toResponse(patient);
    }
}