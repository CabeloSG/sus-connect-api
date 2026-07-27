package br.com.susconnect.application.communication.factory;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.communication.application.factory.CommunicationFactory;
import br.com.susconnect.communication.application.service.ExpirationPolicyService;
import br.com.susconnect.communication.application.service.TokenGeneratorService;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunicationFactoryTest {

    @Mock
    private TokenGeneratorService tokenGeneratorService;

    @Mock
    private ExpirationPolicyService expirationPolicyService;

    private CommunicationFactory communicationFactory;

    @BeforeEach
    void setUp() {
        communicationFactory = new CommunicationFactory(
                tokenGeneratorService,
                expirationPolicyService
        );
    }

    @Test
    void shouldCreateCommunicationWithThreeDeliveries() {

        Appointment appointment = Appointment.builder().build();

        LocalDateTime expirationDate =
                LocalDateTime.of(2026, 8, 8, 10, 0);

        when(expirationPolicyService.calculateExpiration(appointment))
                .thenReturn(expirationDate);

        when(tokenGeneratorService.generate())
                .thenReturn(
                        "token-whatsapp",
                        "token-sms",
                        "token-email"
                );

        Communication communication =
                communicationFactory.create(appointment);

        assertNotNull(communication);
        assertSame(appointment, communication.getAppointment());
        assertEquals(
                CommunicationStatus.PENDING,
                communication.getStatus()
        );
        assertEquals(
                expirationDate,
                communication.getExpirationDate()
        );

        assertNotNull(communication.getDeliveries());
        assertEquals(3, communication.getDeliveries().size());

        verify(expirationPolicyService)
                .calculateExpiration(appointment);

        verify(tokenGeneratorService, times(3))
                .generate();
    }

    @Test
    void shouldCreateDeliveriesForWhatsappSmsAndEmail() {

        Appointment appointment = Appointment.builder().build();

        when(expirationPolicyService.calculateExpiration(appointment))
                .thenReturn(LocalDateTime.of(2026, 8, 8, 10, 0));

        when(tokenGeneratorService.generate())
                .thenReturn(
                        "token-whatsapp",
                        "token-sms",
                        "token-email"
                );

        Communication communication =
                communicationFactory.create(appointment);

        List<NotificationDelivery> deliveries =
                communication.getDeliveries();

        assertEquals(
                NotificationChannel.WHATSAPP,
                deliveries.get(0).getChannel()
        );

        assertEquals(
                NotificationChannel.SMS,
                deliveries.get(1).getChannel()
        );

        assertEquals(
                NotificationChannel.EMAIL,
                deliveries.get(2).getChannel()
        );

        assertTrue(
                deliveries.stream()
                        .allMatch(delivery ->
                                delivery.getStatus()
                                        == DeliveryStatus.CREATED)
        );
    }

    @Test
    void shouldGenerateUniqueTokenForEachDelivery() {

        Appointment appointment = Appointment.builder().build();

        when(expirationPolicyService.calculateExpiration(appointment))
                .thenReturn(LocalDateTime.of(2026, 8, 8, 10, 0));

        when(tokenGeneratorService.generate())
                .thenReturn(
                        "token-whatsapp",
                        "token-sms",
                        "token-email"
                );

        Communication communication =
                communicationFactory.create(appointment);

        List<NotificationDelivery> deliveries =
                communication.getDeliveries();

        assertEquals(
                "token-whatsapp",
                deliveries.get(0).getToken()
        );

        assertEquals(
                "token-sms",
                deliveries.get(1).getToken()
        );

        assertEquals(
                "token-email",
                deliveries.get(2).getToken()
        );

        assertEquals(
                3,
                deliveries.stream()
                        .map(NotificationDelivery::getToken)
                        .distinct()
                        .count()
        );
    }

    @Test
    void shouldAssociateEveryDeliveryWithCreatedCommunication() {

        Appointment appointment = Appointment.builder().build();

        when(expirationPolicyService.calculateExpiration(appointment))
                .thenReturn(LocalDateTime.of(2026, 8, 8, 10, 0));

        when(tokenGeneratorService.generate())
                .thenReturn(
                        "token-whatsapp",
                        "token-sms",
                        "token-email"
                );

        Communication communication =
                communicationFactory.create(appointment);

        assertTrue(
                communication.getDeliveries()
                        .stream()
                        .allMatch(delivery ->
                                delivery.getCommunication()
                                        == communication)
        );
    }
}