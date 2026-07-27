package br.com.susconnect.application.notification.service;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import br.com.susconnect.notification.application.service.PatientNotificationService;
import br.com.susconnect.patient.domain.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Testes unitários do serviço responsável pela
 * comunicação informativa de próxima disponibilidade
 * ao paciente.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
class PatientNotificationServiceTest {

    private PatientNotificationService service;

    @BeforeEach
    void setUp() {
        service = new PatientNotificationService();
    }

    @Test
    void shouldNotifyPatientAboutNextAvailabilitySuccessfully() {

        UUID patientId = UUID.randomUUID();
        UUID cancelledAppointmentId = UUID.randomUUID();
        UUID availableSlotId = UUID.randomUUID();

        Patient patient = Patient.builder()
                .fullName("João da Silva")
                .phone("67999998888")
                .email("joao@email.com")
                .build();

        NextAvailabilityFoundEvent event =
                new NextAvailabilityFoundEvent(
                        patientId,
                        cancelledAppointmentId,
                        availableSlotId,
                        LocalDateTime.of(
                                2026,
                                8,
                                15,
                                14,
                                30
                        ),
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CARDIOLOGIA,
                        "Dr. Carlos Silva",
                        "UBS Central"
                );

        assertDoesNotThrow(() ->
                service.notifyNextAvailability(
                        event,
                        patient
                )
        );
    }
}