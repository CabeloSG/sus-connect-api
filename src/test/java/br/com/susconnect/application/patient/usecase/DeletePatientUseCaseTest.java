package br.com.susconnect.application.patient.usecase;

import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.patient.application.usecase.DeletePatientUseCase;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela exclusão de pacientes.
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
class DeletePatientUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private DeletePatientUseCase useCase;

    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
    }

    @Test
    void shouldDeletePatientSuccessfully() {

        when(patientRepository.existsById(patientId))
                .thenReturn(true);

        when(appointmentRepository.existsByPatient_Id(patientId))
                .thenReturn(false);

        useCase.execute(patientId);

        verify(patientRepository).existsById(patientId);
        verify(appointmentRepository).existsByPatient_Id(patientId);
        verify(patientRepository).deleteById(patientId);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist() {

        when(patientRepository.existsById(patientId))
                .thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(patientId)
        );

        assertEquals(
                "Paciente não encontrado.",
                exception.getMessage()
        );

        verify(patientRepository).existsById(patientId);

        verify(appointmentRepository, never())
                .existsByPatient_Id(patientId);

        verify(patientRepository, never())
                .deleteById(patientId);
    }

    @Test
    void shouldThrowBusinessExceptionWhenPatientHasAppointments() {

        when(patientRepository.existsById(patientId))
                .thenReturn(true);

        when(appointmentRepository.existsByPatient_Id(patientId))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> useCase.execute(patientId)
        );

        assertEquals(
                "Não é possível excluir um paciente que possui agendamentos vinculados.",
                exception.getMessage()
        );

        verify(patientRepository).existsById(patientId);
        verify(appointmentRepository).existsByPatient_Id(patientId);

        verify(patientRepository, never())
                .deleteById(patientId);
    }
}