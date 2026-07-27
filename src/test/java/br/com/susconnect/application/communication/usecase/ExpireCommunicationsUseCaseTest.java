package br.com.susconnect.application.communication.usecase;

import br.com.susconnect.communication.application.command.usecase.ExpireCommunicationsUseCase;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpireCommunicationsUseCaseTest {

    @Mock
    private CommunicationRepository communicationRepository;

    private ExpireCommunicationsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExpireCommunicationsUseCase(
                communicationRepository
        );
    }

    @Test
    void shouldExpirePendingCommunicationAndDeliveries() {

        NotificationDelivery delivery =
                NotificationDelivery.builder()
                        .status(DeliveryStatus.SENT)
                        .build();

        Communication communication =
                Communication.builder()
                        .status(CommunicationStatus.PENDING)
                        .expirationDate(
                                LocalDateTime.now().minusMinutes(10)
                        )
                        .deliveries(
                                new ArrayList<>(List.of(delivery))
                        )
                        .build();

        delivery.setCommunication(communication);

        when(communicationRepository
                .findByStatusAndExpirationDateBefore(
                        eq(CommunicationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(communication));

        int result = useCase.execute();

        assertEquals(1, result);
        assertEquals(
                CommunicationStatus.EXPIRED,
                communication.getStatus()
        );
        assertEquals(
                DeliveryStatus.EXPIRED,
                delivery.getStatus()
        );

        verify(communicationRepository)
                .saveAll(List.of(communication));
    }

    @Test
    void shouldReturnZeroWhenThereAreNoExpiredCommunications() {

        when(communicationRepository
                .findByStatusAndExpirationDateBefore(
                        eq(CommunicationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of());

        int result = useCase.execute();

        assertEquals(0, result);

        verify(communicationRepository)
                .saveAll(List.of());
    }

    @Test
    void shouldNotChangeAlreadyRespondedDelivery() {

        NotificationDelivery delivery =
                NotificationDelivery.builder()
                        .status(DeliveryStatus.RESPONDED)
                        .build();

        Communication communication =
                Communication.builder()
                        .status(CommunicationStatus.PENDING)
                        .expirationDate(
                                LocalDateTime.now().minusMinutes(10)
                        )
                        .deliveries(
                                new ArrayList<>(List.of(delivery))
                        )
                        .build();

        delivery.setCommunication(communication);

        when(communicationRepository
                .findByStatusAndExpirationDateBefore(
                        eq(CommunicationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(communication));

        int result = useCase.execute();

        assertEquals(1, result);
        assertEquals(
                CommunicationStatus.EXPIRED,
                communication.getStatus()
        );

        // RESPONDED não pode virar EXPIRED.
        assertEquals(
                DeliveryStatus.RESPONDED,
                delivery.getStatus()
        );
    }
}