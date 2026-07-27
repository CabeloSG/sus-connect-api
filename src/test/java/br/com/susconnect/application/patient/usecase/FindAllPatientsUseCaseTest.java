package br.com.susconnect.application.patient.usecase;

import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.application.usecase.FindAllPatientsUseCase;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.domain.enums.PatientStatus;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela listagem de pacientes.
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
class FindAllPatientsUseCaseTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private PatientMapper mapper;

    @InjectMocks
    private FindAllPatientsUseCase useCase;

    @Test
    void shouldFindAllPatientsSuccessfully() {

        Patient patient1 = Patient.builder()
                .fullName("João da Silva")
                .status(PatientStatus.ACTIVE)
                .build();

        Patient patient2 = Patient.builder()
                .fullName("Maria Oliveira")
                .status(PatientStatus.ACTIVE)
                .build();

        PatientResponse response1 = PatientResponse.builder()
                .id(UUID.randomUUID())
                .fullName("João da Silva")
                .status(PatientStatus.ACTIVE)
                .build();

        PatientResponse response2 = PatientResponse.builder()
                .id(UUID.randomUUID())
                .fullName("Maria Oliveira")
                .status(PatientStatus.ACTIVE)
                .build();

        when(repository.findAll())
                .thenReturn(List.of(patient1, patient2));

        when(mapper.toResponse(patient1))
                .thenReturn(response1);

        when(mapper.toResponse(patient2))
                .thenReturn(response2);

        List<PatientResponse> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("João da Silva", result.get(0).getFullName());
        assertEquals("Maria Oliveira", result.get(1).getFullName());

        verify(repository).findAll();
        verify(mapper).toResponse(patient1);
        verify(mapper).toResponse(patient2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoPatients() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<PatientResponse> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
        verify(mapper, never()).toResponse(
                org.mockito.ArgumentMatchers.any(Patient.class)
        );
    }
}