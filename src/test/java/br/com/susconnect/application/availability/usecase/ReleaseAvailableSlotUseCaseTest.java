package br.com.susconnect.application.availability.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import br.com.susconnect.availability.application.usecase.ReleaseAvailableSlotUseCase;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.messaging.AvailableSlotEventPublisher;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import br.com.susconnect.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela liberação de vagas.
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
class ReleaseAvailableSlotUseCaseTest {

    @Mock
    private AvailableSlotRepository availableSlotRepository;

    @Mock
    private AvailableSlotEventPublisher availableSlotEventPublisher;

    @InjectMocks
    private ReleaseAvailableSlotUseCase useCase;

    private UUID appointmentId;
    private Appointment appointment;

    @BeforeEach
    void setUp() {

        appointmentId = UUID.randomUUID();

        appointment = Appointment.builder()
                .appointmentDateTime(
                        LocalDateTime.of(2026, 8, 20, 9, 0)
                )
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .build();

        appointment.setId(appointmentId);
    }

    @Test
    void shouldReleaseAvailableSlotSuccessfully() {

        UUID slotId = UUID.randomUUID();

        when(availableSlotRepository
                .existsBySourceAppointmentId(appointmentId))
                .thenReturn(false);

        when(availableSlotRepository.save(any(AvailableSlot.class)))
                .thenAnswer(invocation -> {

                    AvailableSlot slot =
                            invocation.getArgument(0);

                    slot.setId(slotId);

                    return slot;
                });

        AvailableSlot result =
                useCase.execute(appointment);

        assertEquals(slotId, result.getId());

        assertSame(
                appointment,
                result.getSourceAppointment()
        );

        assertEquals(
                appointment.getAppointmentDateTime(),
                result.getAppointmentDateTime()
        );

        assertEquals(
                AppointmentType.CONSULTATION,
                result.getAppointmentType()
        );

        assertEquals(
                MedicalSpecialty.CLINICO_GERAL,
                result.getMedicalSpecialty()
        );

        assertEquals(
                "Dr. Carlos Silva",
                result.getDoctor()
        );

        assertEquals(
                "UBS Central",
                result.getHealthUnit()
        );

        assertEquals(
                AvailableSlotStatus.AVAILABLE,
                result.getStatus()
        );

        verify(availableSlotRepository)
                .existsBySourceAppointmentId(appointmentId);

        verify(availableSlotRepository)
                .save(any(AvailableSlot.class));

        ArgumentCaptor<AvailableSlotReleasedEvent> eventCaptor =
                ArgumentCaptor.forClass(
                        AvailableSlotReleasedEvent.class
                );

        verify(availableSlotEventPublisher)
                .publish(eventCaptor.capture());

        AvailableSlotReleasedEvent event =
                eventCaptor.getValue();

        assertEquals(slotId, event.availableSlotId());

        assertEquals(
                appointmentId,
                event.sourceAppointmentId()
        );

        assertEquals(
                appointment.getAppointmentDateTime(),
                event.appointmentDateTime()
        );

        assertEquals(
                appointment.getAppointmentType(),
                event.appointmentType()
        );

        assertEquals(
                appointment.getMedicalSpecialty(),
                event.medicalSpecialty()
        );

        assertEquals(
                appointment.getDoctor(),
                event.doctor()
        );

        assertEquals(
                appointment.getHealthUnit(),
                event.healthUnit()
        );
    }

    @Test
    void shouldThrowBusinessExceptionWhenAppointmentIsInvalid() {

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> useCase.execute(null)
                );

        assertEquals(
                "Agendamento inválido para liberação da vaga.",
                exception.getMessage()
        );

        verify(availableSlotRepository, never())
                .save(any());

        verify(availableSlotEventPublisher, never())
                .publish(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenSlotAlreadyExists() {

        when(availableSlotRepository
                .existsBySourceAppointmentId(appointmentId))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> useCase.execute(appointment)
                );

        assertEquals(
                "Já existe uma vaga liberada para este agendamento.",
                exception.getMessage()
        );

        verify(availableSlotRepository)
                .existsBySourceAppointmentId(appointmentId);

        verify(availableSlotRepository, never())
                .save(any());

        verify(availableSlotEventPublisher, never())
                .publish(any());
    }
}