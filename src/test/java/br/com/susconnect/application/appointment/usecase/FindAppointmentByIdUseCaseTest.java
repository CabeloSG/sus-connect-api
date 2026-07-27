package br.com.susconnect.application.appointment.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.application.usecase.FindAppointmentByIdUseCase;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela busca de agendamento por ID.
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
class FindAppointmentByIdUseCaseTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentMapper mapper;

    @InjectMocks
    private FindAppointmentByIdUseCase useCase;

    private UUID appointmentId;
    private Appointment appointment;
    private AppointmentResponse response;

    @BeforeEach
    void setUp() {

        appointmentId = UUID.randomUUID();

        appointment = Appointment.builder()
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .build();

        response = AppointmentResponse.builder()
                .id(appointmentId)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .build();
    }

    @Test
    void shouldFindAppointmentByIdSuccessfully() {

        when(repository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(mapper.toResponse(appointment))
                .thenReturn(response);

        AppointmentResponse result =
                useCase.execute(appointmentId);

        assertEquals(appointmentId, result.getId());
        assertEquals("Dr. Carlos Silva", result.getDoctor());
        assertEquals("UBS Central", result.getHealthUnit());
        assertEquals(
                AppointmentStatus.PENDING_CONFIRMATION,
                result.getStatus()
        );

        verify(repository).findById(appointmentId);
        verify(mapper).toResponse(appointment);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist() {

        when(repository.findById(appointmentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(appointmentId)
        );

        assertEquals(
                "Agendamento não encontrado.",
                exception.getMessage()
        );

        verify(repository).findById(appointmentId);
        verify(mapper, never()).toResponse(appointment);
    }
}