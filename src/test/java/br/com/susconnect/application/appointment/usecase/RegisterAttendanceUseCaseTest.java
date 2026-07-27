package br.com.susconnect.application.appointment.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.dto.RegisterAttendanceRequest;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.application.usecase.RegisterAttendanceUseCase;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAttendanceUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    private RegisterAttendanceUseCase useCase;

    private UUID appointmentId;

    @BeforeEach
    void setUp() {

        useCase = new RegisterAttendanceUseCase(
                appointmentRepository,
                appointmentMapper
        );

        appointmentId = UUID.randomUUID();
    }

    @Test
    void shouldMarkAppointmentAsCompletedWhenPatientAttended() {

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(
                        LocalDateTime.now().minusHours(1)
                )
                .status(AppointmentStatus.CONFIRMED)
                .build();

        RegisterAttendanceRequest request =
                new RegisterAttendanceRequest(true);

        AppointmentResponse response =
                AppointmentResponse.builder()
                        .status(AppointmentStatus.COMPLETED)
                        .build();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(response);

        AppointmentResponse result =
                useCase.execute(appointmentId, request);

        assertEquals(
                AppointmentStatus.COMPLETED,
                appointment.getStatus()
        );

        assertEquals(
                AppointmentStatus.COMPLETED,
                result.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldMarkAppointmentAsNoShowWhenPatientDidNotAttend() {

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(
                        LocalDateTime.now().minusHours(1)
                )
                .status(AppointmentStatus.CONFIRMED)
                .build();

        RegisterAttendanceRequest request =
                new RegisterAttendanceRequest(false);

        AppointmentResponse response =
                AppointmentResponse.builder()
                        .status(AppointmentStatus.NO_SHOW)
                        .build();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(response);

        AppointmentResponse result =
                useCase.execute(appointmentId, request);

        assertEquals(
                AppointmentStatus.NO_SHOW,
                appointment.getStatus()
        );

        assertEquals(
                AppointmentStatus.NO_SHOW,
                result.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentDoesNotExist() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        RegisterAttendanceRequest request =
                new RegisterAttendanceRequest(true);

        assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(appointmentId, request)
        );

        verify(appointmentRepository, never())
                .save(any());
    }

    @Test
    void shouldNotAllowAttendanceBeforeAppointmentTime() {

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(
                        LocalDateTime.now().plusDays(1)
                )
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        RegisterAttendanceRequest request =
                new RegisterAttendanceRequest(true);

        assertThrows(
                BusinessException.class,
                () -> useCase.execute(appointmentId, request)
        );

        verify(appointmentRepository, never())
                .save(any());
    }

    @Test
    void shouldNotAllowAttendanceForCancelledAppointment() {

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(
                        LocalDateTime.now().minusHours(1)
                )
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        RegisterAttendanceRequest request =
                new RegisterAttendanceRequest(true);

        assertThrows(
                BusinessException.class,
                () -> useCase.execute(appointmentId, request)
        );

        verify(appointmentRepository, never())
                .save(any());
    }

    @Test
    void shouldNotAllowAttendanceWhenOutcomeWasAlreadyRegistered() {

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(
                        LocalDateTime.now().minusHours(1)
                )
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        RegisterAttendanceRequest request =
                new RegisterAttendanceRequest(true);

        assertThrows(
                BusinessException.class,
                () -> useCase.execute(appointmentId, request)
        );

        verify(appointmentRepository, never())
                .save(any());
    }
}