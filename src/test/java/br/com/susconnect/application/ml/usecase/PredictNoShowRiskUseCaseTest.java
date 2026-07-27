package br.com.susconnect.application.ml.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import br.com.susconnect.ml.application.dto.NoShowPredictionRequest;
import br.com.susconnect.ml.application.dto.NoShowPredictionResponse;
import br.com.susconnect.ml.application.usecase.PredictNoShowRiskUseCase;
import br.com.susconnect.ml.infrastructure.client.NoShowMlClient;
import br.com.susconnect.patient.domain.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictNoShowRiskUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CommunicationRepository communicationRepository;

    @Mock
    private NoShowMlClient noShowMlClient;

    @InjectMocks
    private PredictNoShowRiskUseCase useCase;

    private UUID appointmentId;
    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    void setUp() {

        appointmentId = UUID.randomUUID();

        patient = Patient.builder()
                .fullName("João da Silva")
                .birthDate(LocalDate.of(1990, 8, 15))
                .build();

        appointment = Appointment.builder()
                .patient(patient)
                .appointmentDateTime(
                        LocalDateTime.of(2026, 8, 20, 14, 30))
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .build();

        appointment.setId(appointmentId);
        appointment.setCreatedAt(
                LocalDateTime.of(2026, 8, 10, 10, 0));
    }

    @Test
    void shouldPredictNoShowRiskUsingPatientHistory() {

        Appointment noShow = Appointment.builder()
                .patient(patient)
                .appointmentDateTime(
                        LocalDateTime.of(2026, 6, 10, 9, 0))
                .status(AppointmentStatus.NO_SHOW)
                .build();

        noShow.setId(UUID.randomUUID());

        Appointment completed = Appointment.builder()
                .patient(patient)
                .appointmentDateTime(
                        LocalDateTime.of(2026, 7, 10, 9, 0))
                .status(AppointmentStatus.COMPLETED)
                .build();

        completed.setId(UUID.randomUUID());

        // Agendamento futuro não deve entrar no histórico.
        Appointment future = Appointment.builder()
                .patient(patient)
                .appointmentDateTime(
                        LocalDateTime.of(2026, 9, 10, 9, 0))
                .status(AppointmentStatus.NO_SHOW)
                .build();

        future.setId(UUID.randomUUID());

        Communication communication =
                Communication.builder()
                        .status(CommunicationStatus.CONFIRMED)
                        .build();

        NoShowPredictionResponse expected =
                new NoShowPredictionResponse(
                        0.75,
                        "HIGH"
                );

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.findByPatient(patient))
                .thenReturn(List.of(
                        appointment,
                        noShow,
                        completed,
                        future
                ));

        when(communicationRepository
                .findByAppointmentId(appointmentId))
                .thenReturn(Optional.of(communication));

        when(noShowMlClient.predict(
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(expected);

        NoShowPredictionResponse result =
                useCase.execute(appointmentId);

        assertEquals(expected, result);

        ArgumentCaptor<NoShowPredictionRequest> captor =
                ArgumentCaptor.forClass(
                        NoShowPredictionRequest.class);

        verify(noShowMlClient).predict(captor.capture());

        NoShowPredictionRequest request =
                captor.getValue();

        assertEquals(36, request.patientAge());
        assertEquals(4, request.dayOfWeek());
        assertEquals(14, request.hourOfDay());
        assertEquals(10, request.daysInAdvance());

        assertEquals(
                "CONSULTATION",
                request.appointmentType());

        assertEquals(
                "CLINICO_GERAL",
                request.medicalSpecialty());

        assertEquals(
                2L,
                request.historicalAppointments());

        assertEquals(
                1L,
                request.historicalNoShows());

        assertEquals(
                1L,
                request.historicalCompleted());

        assertEquals(
                0.5,
                request.historicalNoShowRate(),
                0.0001);

        assertEquals(
                "CONFIRMED",
                request.confirmationStatus());
    }

    @Test
    void shouldPredictWithZeroHistoryAndPendingConfirmation() {

        NoShowPredictionResponse expected =
                new NoShowPredictionResponse(
                        0.20,
                        "LOW"
                );

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.findByPatient(patient))
                .thenReturn(List.of(appointment));

        when(communicationRepository
                .findByAppointmentId(appointmentId))
                .thenReturn(Optional.empty());

        when(noShowMlClient.predict(
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(expected);

        NoShowPredictionResponse result =
                useCase.execute(appointmentId);

        assertEquals(expected, result);

        ArgumentCaptor<NoShowPredictionRequest> captor =
                ArgumentCaptor.forClass(
                        NoShowPredictionRequest.class);

        verify(noShowMlClient)
                .predict(captor.capture());

        NoShowPredictionRequest request =
                captor.getValue();

        assertEquals(
                0L,
                request.historicalAppointments());

        assertEquals(
                0L,
                request.historicalNoShows());

        assertEquals(
                0L,
                request.historicalCompleted());

        assertEquals(
                0.0,
                request.historicalNoShowRate(),
                0.0001);

        assertEquals(
                "PENDING",
                request.confirmationStatus());
    }

    @Test
    void shouldThrowResourceNotFoundWhenAppointmentDoesNotExist() {

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

        verify(appointmentRepository, never())
                .findByPatient(
                        org.mockito.ArgumentMatchers.any());

        verify(communicationRepository, never())
                .findByAppointmentId(
                        org.mockito.ArgumentMatchers.any());

        verify(noShowMlClient, never())
                .predict(
                        org.mockito.ArgumentMatchers.any());
    }
}