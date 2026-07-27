package br.com.susconnect.application.availability.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.dto.NextAvailabilityResponse;
import br.com.susconnect.availability.application.mapper.NextAvailabilityMapper;
import br.com.susconnect.availability.application.usecase.FindNextAvailabilityUseCase;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import br.com.susconnect.common.exception.BusinessException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela consulta da próxima disponibilidade.
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
class FindNextAvailabilityUseCaseTest {

    @Mock
    private AvailableSlotRepository availableSlotRepository;

    @Mock
    private NextAvailabilityMapper nextAvailabilityMapper;

    @InjectMocks
    private FindNextAvailabilityUseCase useCase;

    private Appointment appointment;
    private LocalDateTime appointmentDateTime;

    @BeforeEach
    void setUp() {

        appointmentDateTime =
                LocalDateTime.of(2026, 8, 20, 9, 0);

        appointment = Appointment.builder()
                .appointmentDateTime(appointmentDateTime)
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .build();
    }

    @Test
    void shouldReturnNextAvailabilityWhenCompatibleSlotExists() {

        UUID availableSlotId = UUID.randomUUID();

        LocalDateTime nextDate =
                LocalDateTime.of(2026, 8, 22, 10, 30);

        AvailableSlot availableSlot =
                AvailableSlot.builder()
                        .appointmentDateTime(nextDate)
                        .appointmentType(AppointmentType.CONSULTATION)
                        .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                        .doctor("Dra. Maria Souza")
                        .healthUnit("UBS Norte")
                        .status(AvailableSlotStatus.AVAILABLE)
                        .build();

        availableSlot.setId(availableSlotId);

        NextAvailabilityResponse expectedResponse =
                new NextAvailabilityResponse(
                        availableSlotId,
                        nextDate,
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CLINICO_GERAL,
                        "Dra. Maria Souza",
                        "UBS Norte"
                );

        when(availableSlotRepository
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        AvailableSlotStatus.AVAILABLE,
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CLINICO_GERAL,
                        appointmentDateTime
                ))
                .thenReturn(Optional.of(availableSlot));

        when(nextAvailabilityMapper.toResponse(availableSlot))
                .thenReturn(expectedResponse);

        Optional<NextAvailabilityResponse> result =
                useCase.execute(appointment);

        assertTrue(result.isPresent());

        assertEquals(
                expectedResponse,
                result.get()
        );

        verify(availableSlotRepository)
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        AvailableSlotStatus.AVAILABLE,
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CLINICO_GERAL,
                        appointmentDateTime
                );

        verify(nextAvailabilityMapper)
                .toResponse(availableSlot);
    }

    @Test
    void shouldReturnEmptyWhenCompatibleSlotDoesNotExist() {

        when(availableSlotRepository
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        AvailableSlotStatus.AVAILABLE,
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CLINICO_GERAL,
                        appointmentDateTime
                ))
                .thenReturn(Optional.empty());

        Optional<NextAvailabilityResponse> result =
                useCase.execute(appointment);

        assertTrue(result.isEmpty());

        verify(availableSlotRepository)
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        AvailableSlotStatus.AVAILABLE,
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CLINICO_GERAL,
                        appointmentDateTime
                );

        verify(nextAvailabilityMapper, never())
                .toResponse(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenAppointmentIsNull() {

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> useCase.execute(null)
                );

        assertEquals(
                "Agendamento inválido para consulta de disponibilidade.",
                exception.getMessage()
        );

        verify(availableSlotRepository, never())
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                );

        verify(nextAvailabilityMapper, never())
                .toResponse(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenAppointmentDoesNotHaveRequiredData() {

        Appointment invalidAppointment =
                Appointment.builder()
                        .doctor("Dr. Carlos Silva")
                        .healthUnit("UBS Central")
                        .build();

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> useCase.execute(invalidAppointment)
                );

        assertEquals(
                "Agendamento não possui os dados necessários para consultar a próxima disponibilidade.",
                exception.getMessage()
        );

        verify(availableSlotRepository, never())
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                );

        verify(nextAvailabilityMapper, never())
                .toResponse(org.mockito.ArgumentMatchers.any());
    }
}