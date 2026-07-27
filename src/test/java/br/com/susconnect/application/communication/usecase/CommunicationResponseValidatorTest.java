package br.com.susconnect.application.communication.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.communication.application.validator.CommunicationResponseValidator;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.NotificationChannel;
import br.com.susconnect.communication.domain.enums.PatientResponse;
import br.com.susconnect.communication.infrastructure.persistence.NotificationDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários das regras de validação
 * da resposta do paciente.
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
class CommunicationResponseValidatorTest {

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @InjectMocks
    private CommunicationResponseValidator validator;

    private Communication communication;
    private NotificationDelivery delivery;

    @BeforeEach
    void setUp() {

        Appointment appointment = Appointment.builder()
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .build();

        communication = Communication.builder()
                .appointment(appointment)
                .status(CommunicationStatus.PENDING)
                .expirationDate(LocalDateTime.now().plusDays(1))
                .build();

        communication.setId(UUID.randomUUID());

        delivery = NotificationDelivery.builder()
                .communication(communication)
                .channel(NotificationChannel.SMS)
                .token("token-sus-connect")
                .status(DeliveryStatus.SENT)
                .build();

        delivery.setId(UUID.randomUUID());
    }

    @Test
    void shouldAcceptSentDelivery() {

        delivery.setStatus(DeliveryStatus.SENT);

        assertDoesNotThrow(
                () -> validator.validateStatus(delivery)
        );
    }

    @Test
    void shouldAcceptDeliveredDelivery() {

        delivery.setStatus(DeliveryStatus.DELIVERED);

        assertDoesNotThrow(
                () -> validator.validateStatus(delivery)
        );
    }

    @Test
    void shouldAcceptOpenedDelivery() {

        delivery.setStatus(DeliveryStatus.OPENED);

        assertDoesNotThrow(
                () -> validator.validateStatus(delivery)
        );
    }

    @Test
    void shouldRejectCreatedDelivery() {

        delivery.setStatus(DeliveryStatus.CREATED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateStatus(delivery)
        );

        assertEquals(
                "A notificação ainda não foi enviada ao paciente.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectFailedDelivery() {

        delivery.setStatus(DeliveryStatus.FAILED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateStatus(delivery)
        );

        assertEquals(
                "Não foi possível entregar esta notificação ao paciente.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectExpiredDelivery() {

        delivery.setStatus(DeliveryStatus.EXPIRED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateStatus(delivery)
        );

        assertEquals(
                "Esta notificação expirou.",
                exception.getMessage()
        );
    }

    @Test
    void shouldInformPreviousConfirmationChannel() {

        delivery.setStatus(DeliveryStatus.INVALIDATED);

        NotificationDelivery respondedDelivery =
                NotificationDelivery.builder()
                        .communication(communication)
                        .channel(NotificationChannel.SMS)
                        .status(DeliveryStatus.RESPONDED)
                        .patientResponse(PatientResponse.YES)
                        .build();

        when(notificationDeliveryRepository.findByCommunicationId(
                communication.getId()))
                .thenReturn(List.of(delivery, respondedDelivery));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateStatus(delivery)
        );

        assertEquals(
                "Este agendamento já foi confirmado anteriormente pelo canal SMS.",
                exception.getMessage()
        );

        verify(notificationDeliveryRepository)
                .findByCommunicationId(communication.getId());
    }

    @Test
    void shouldInformPreviousDeclineChannel() {

        delivery.setStatus(DeliveryStatus.INVALIDATED);

        NotificationDelivery respondedDelivery =
                NotificationDelivery.builder()
                        .communication(communication)
                        .channel(NotificationChannel.SMS)
                        .status(DeliveryStatus.RESPONDED)
                        .patientResponse(PatientResponse.NO)
                        .build();

        when(notificationDeliveryRepository.findByCommunicationId(
                communication.getId()))
                .thenReturn(List.of(delivery, respondedDelivery));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateStatus(delivery)
        );

        assertEquals(
                "Este agendamento já foi recusado anteriormente pelo canal SMS.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnavailableDeliveryWhenNoPreviousResponseExists() {

        delivery.setStatus(DeliveryStatus.INVALIDATED);

        when(notificationDeliveryRepository.findByCommunicationId(
                communication.getId()))
                .thenReturn(List.of(delivery));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateStatus(delivery)
        );

        assertEquals(
                "Esta notificação não está mais disponível para resposta.",
                exception.getMessage()
        );
    }

    @Test
    void shouldAcceptCommunicationInsideConfirmationDeadline() {

        communication.setExpirationDate(
                LocalDateTime.now().plusHours(1)
        );

        assertDoesNotThrow(
                () -> validator.validateCommunicationExpiration(delivery)
        );
    }

    @Test
    void shouldRejectExpiredCommunication() {

        communication.setExpirationDate(
                LocalDateTime.now().minusMinutes(1)
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validateCommunicationExpiration(delivery)
        );

        assertEquals(
                "O prazo para confirmação desta consulta expirou.",
                exception.getMessage()
        );
    }
}