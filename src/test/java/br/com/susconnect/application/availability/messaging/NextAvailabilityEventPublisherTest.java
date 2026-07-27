package br.com.susconnect.application.availability.messaging;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import br.com.susconnect.availability.infrastructure.messaging.NextAvailabilityEventPublisher;
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
class NextAvailabilityEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private NextAvailabilityEventPublisher publisher;

    @BeforeEach
    void setUp() {

        publisher =
                new NextAvailabilityEventPublisher(
                        kafkaTemplate
                );
    }

    @Test
    void shouldPublishNextAvailabilityFoundEventToKafka() {

        UUID patientId =
                UUID.randomUUID();

        UUID cancelledAppointmentId =
                UUID.randomUUID();

        UUID availableSlotId =
                UUID.randomUUID();

        NextAvailabilityFoundEvent event =
                new NextAvailabilityFoundEvent(
                        patientId,
                        cancelledAppointmentId,
                        availableSlotId,
                        LocalDateTime.of(
                                2026,
                                8,
                                25,
                                9,
                                30
                        ),
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CARDIOLOGIA,
                        "Dr. Carlos",
                        "UBS Central"
                );

        publisher.publish(event);

        verify(kafkaTemplate).send(
                "next-availability-found",
                patientId.toString(),
                event
        );

        verifyNoMoreInteractions(kafkaTemplate);
    }
}