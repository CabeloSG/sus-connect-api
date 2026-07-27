package br.com.susconnect.application.appointment.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.application.usecase.FindAllAppointmentsUseCase;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela listagem de agendamentos.
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
class FindAllAppointmentsUseCaseTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentMapper mapper;

    @InjectMocks
    private FindAllAppointmentsUseCase useCase;

    @Test
    void shouldFindAllAppointmentsSuccessfully() {

        Appointment appointment1 = Appointment.builder()
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .build();

        Appointment appointment2 = Appointment.builder()
                .doctor("Dra. Ana Souza")
                .healthUnit("UBS Norte")
                .status(AppointmentStatus.CONFIRMED)
                .build();

        AppointmentResponse response1 = AppointmentResponse.builder()
                .id(UUID.randomUUID())
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .build();

        AppointmentResponse response2 = AppointmentResponse.builder()
                .id(UUID.randomUUID())
                .doctor("Dra. Ana Souza")
                .healthUnit("UBS Norte")
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(repository.findAll())
                .thenReturn(List.of(appointment1, appointment2));

        when(mapper.toResponse(appointment1))
                .thenReturn(response1);

        when(mapper.toResponse(appointment2))
                .thenReturn(response2);

        List<AppointmentResponse> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "Dr. Carlos Silva",
                result.get(0).getDoctor()
        );

        assertEquals(
                "Dra. Ana Souza",
                result.get(1).getDoctor()
        );

        assertEquals(
                AppointmentStatus.PENDING_CONFIRMATION,
                result.get(0).getStatus()
        );

        assertEquals(
                AppointmentStatus.CONFIRMED,
                result.get(1).getStatus()
        );

        verify(repository).findAll();
        verify(mapper).toResponse(appointment1);
        verify(mapper).toResponse(appointment2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoAppointments() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<AppointmentResponse> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();

        verify(mapper, never())
                .toResponse(any(Appointment.class));
    }
}