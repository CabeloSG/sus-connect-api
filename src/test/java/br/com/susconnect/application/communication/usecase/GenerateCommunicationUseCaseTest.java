package br.com.susconnect.application.communication.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.application.command.usecase.GenerateCommunicationUseCase;
import br.com.susconnect.communication.application.factory.CommunicationFactory;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela geração de comunicações.
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
class GenerateCommunicationUseCaseTest {

    @Mock
    private CommunicationFactory communicationFactory;

    @Mock
    private CommunicationRepository communicationRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private GenerateCommunicationUseCase useCase;

    private UUID appointmentId;
    private Appointment appointment;
    private Communication communication;

    @BeforeEach
    void setUp() {

        appointmentId = UUID.randomUUID();

        appointment = Appointment.builder()
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .build();

        appointment.setId(appointmentId);

        communication = Communication.builder()
                .appointment(appointment)
                .status(CommunicationStatus.PENDING)
                .expirationDate(
                        LocalDateTime.now().plusDays(1)
                )
                .build();
    }

    @Test
    void shouldGenerateCommunicationSuccessfully() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(communicationRepository.findByAppointmentId(appointmentId))
                .thenReturn(Optional.empty());

        when(communicationFactory.create(appointment))
                .thenReturn(communication);

        when(communicationRepository.save(communication))
                .thenReturn(communication);

        Communication result =
                useCase.execute(appointmentId);

        assertSame(communication, result);

        assertEquals(
                CommunicationStatus.PENDING,
                result.getStatus()
        );

        assertSame(
                appointment,
                result.getAppointment()
        );

        verify(appointmentRepository)
                .findById(appointmentId);

        verify(communicationRepository)
                .findByAppointmentId(appointmentId);

        verify(communicationFactory)
                .create(appointment);

        verify(communicationRepository)
                .save(communication);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> useCase.execute(appointmentId)
                );

        assertEquals(
                "Agendamento não encontrado.",
                exception.getMessage()
        );

        verify(appointmentRepository)
                .findById(appointmentId);

        verify(communicationRepository, never())
                .findByAppointmentId(appointmentId);

        verify(communicationFactory, never())
                .create(appointment);

        verify(communicationRepository, never())
                .save(communication);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCommunicationAlreadyExists() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(communicationRepository.findByAppointmentId(appointmentId))
                .thenReturn(Optional.of(communication));

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> useCase.execute(appointmentId)
                );

        assertEquals(
                "Já existe uma comunicação para este agendamento.",
                exception.getMessage()
        );

        verify(appointmentRepository)
                .findById(appointmentId);

        verify(communicationRepository)
                .findByAppointmentId(appointmentId);

        verify(communicationFactory, never())
                .create(appointment);

        verify(communicationRepository, never())
                .save(communication);
    }
}