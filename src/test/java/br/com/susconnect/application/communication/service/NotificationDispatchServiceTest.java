package br.com.susconnect.application.communication.service;

import br.com.susconnect.communication.application.service.NotificationDispatchService;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.NotificationChannel;
import br.com.susconnect.communication.infrastructure.persistence.NotificationDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDispatchServiceTest {

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    private NotificationDispatchService service;

    @BeforeEach
    void setUp() {

        service = new NotificationDispatchService(
                notificationDeliveryRepository
        );
    }

    @Test
    void shouldDispatchAllPendingNotifications() {

        NotificationDelivery whatsapp =
                NotificationDelivery.builder()
                        .channel(NotificationChannel.WHATSAPP)
                        .token("token-whatsapp")
                        .status(DeliveryStatus.CREATED)
                        .build();

        NotificationDelivery sms =
                NotificationDelivery.builder()
                        .channel(NotificationChannel.SMS)
                        .token("token-sms")
                        .status(DeliveryStatus.CREATED)
                        .build();

        when(notificationDeliveryRepository.findByStatus(
                DeliveryStatus.CREATED
        )).thenReturn(List.of(whatsapp, sms));

        int processed =
                service.dispatchPendingNotifications();

        assertEquals(2, processed);

        assertEquals(
                DeliveryStatus.SENT,
                whatsapp.getStatus()
        );

        assertEquals(
                DeliveryStatus.SENT,
                sms.getStatus()
        );

        verify(notificationDeliveryRepository)
                .save(whatsapp);

        verify(notificationDeliveryRepository)
                .save(sms);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoPendingNotifications() {

        when(notificationDeliveryRepository.findByStatus(
                DeliveryStatus.CREATED
        )).thenReturn(List.of());

        int processed =
                service.dispatchPendingNotifications();

        assertEquals(0, processed);

        verify(
                notificationDeliveryRepository,
                never()
        ).save(any());
    }
}