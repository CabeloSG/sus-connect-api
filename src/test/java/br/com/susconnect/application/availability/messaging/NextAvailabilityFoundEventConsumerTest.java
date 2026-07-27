package br.com.susconnect.application.availability.messaging;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import br.com.susconnect.availability.infrastructure.messaging.NextAvailabilityFoundEventConsumer;
import br.com.susconnect.notification.application.service.PatientNotificationService;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NextAvailabilityFoundEventConsumerTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientNotificationService patientNotificationService;

    private NextAvailabilityFoundEventConsumer consumer;

    @BeforeEach
    void setUp() {

        consumer =
                new NextAvailabilityFoundEventConsumer(
                        patientRepository,
                        patientNotificationService
                );
    }

    @Test
    void shouldNotifyPatientWhenPatientExists() {

        UUID patientId = UUID.randomUUID();

        NextAvailabilityFoundEvent event =
                createEvent(patientId);

        Patient patient =
                Patient.builder()
                        .fullName("João da Silva")
                        .phone("67999999999")
                        .email("joao@email.com")
                        .build();

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));

        consumer.consume(event);

        verify(patientRepository)
                .findById(patientId);

        verify(patientNotificationService)
                .notifyNextAvailability(
                        event,
                        patient
                );

        verifyNoMoreInteractions(
                patientRepository,
                patientNotificationService
        );
    }

    @Test
    void shouldNotNotifyPatientWhenPatientDoesNotExist() {

        UUID patientId = UUID.randomUUID();

        NextAvailabilityFoundEvent event =
                createEvent(patientId);

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.empty());

        consumer.consume(event);

        verify(patientRepository)
                .findById(patientId);

        verifyNoInteractions(
                patientNotificationService
        );

        verifyNoMoreInteractions(
                patientRepository
        );
    }

    private NextAvailabilityFoundEvent createEvent(
            UUID patientId) {

        return new NextAvailabilityFoundEvent(
                patientId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.of(
                        2026, 8, 25, 9, 30
                ),
                AppointmentType.CONSULTATION,
                MedicalSpecialty.CARDIOLOGIA,
                "Dra. Ana",
                "UBS Central"
        );
    }
}