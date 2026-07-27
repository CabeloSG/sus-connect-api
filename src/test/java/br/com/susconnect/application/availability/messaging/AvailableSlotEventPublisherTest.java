package br.com.susconnect.application.availability.messaging;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import br.com.susconnect.availability.infrastructure.messaging.AvailableSlotEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AvailableSlotEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private AvailableSlotEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher =
                new AvailableSlotEventPublisher(kafkaTemplate);
    }

    @Test
    void shouldPublishAvailableSlotReleasedEventToKafka() {

        UUID availableSlotId = UUID.randomUUID();
        UUID sourceAppointmentId = UUID.randomUUID();

        AvailableSlotReleasedEvent event =
                new AvailableSlotReleasedEvent(
                        availableSlotId,
                        sourceAppointmentId,
                        LocalDateTime.of(
                                2026, 8, 20, 14, 30
                        ),
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CARDIOLOGIA,
                        "Dr. Carlos",
                        "UBS Central"
                );

        publisher.publish(event);

        verify(kafkaTemplate).send(
                "available-slot-released",
                availableSlotId.toString(),
                event
        );

        verifyNoMoreInteractions(kafkaTemplate);
    }
}