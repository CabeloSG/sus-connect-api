package br.com.susconnect.application.notification.service;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import br.com.susconnect.notification.application.service.UnitNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Testes unitários do serviço responsável por
 * notificar a unidade de saúde sobre uma vaga
 * disponibilizada para reaproveitamento.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
class UnitNotificationServiceTest {

    private UnitNotificationService service;

    @BeforeEach
    void setUp() {
        service = new UnitNotificationService();
    }

    @Test
    void shouldNotifyHealthUnitAboutAvailableSlotSuccessfully() {

        UUID availableSlotId = UUID.randomUUID();
        UUID sourceAppointmentId = UUID.randomUUID();

        AvailableSlotReleasedEvent event =
                new AvailableSlotReleasedEvent(
                        availableSlotId,
                        sourceAppointmentId,
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
                service.notifyAvailableSlot(event)
        );
    }
}