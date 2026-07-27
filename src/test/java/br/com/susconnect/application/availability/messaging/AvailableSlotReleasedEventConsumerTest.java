package br.com.susconnect.application.availability.messaging;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import br.com.susconnect.availability.infrastructure.messaging.AvailableSlotReleasedEventConsumer;
import br.com.susconnect.notification.application.service.UnitNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AvailableSlotReleasedEventConsumerTest {

    @Mock
    private UnitNotificationService unitNotificationService;

    private AvailableSlotReleasedEventConsumer consumer;

    @BeforeEach
    void setUp() {

        consumer =
                new AvailableSlotReleasedEventConsumer(
                        unitNotificationService
                );
    }

    @Test
    void shouldNotifyHealthUnitWhenAvailableSlotReleasedEventIsConsumed() {

        AvailableSlotReleasedEvent event =
                new AvailableSlotReleasedEvent(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        LocalDateTime.of(
                                2026, 8, 20, 14, 30
                        ),
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CARDIOLOGIA,
                        "Dr. Carlos",
                        "UBS Central"
                );

        consumer.consume(event);

        verify(unitNotificationService)
                .notifyAvailableSlot(event);

        verifyNoMoreInteractions(
                unitNotificationService
        );
    }
}