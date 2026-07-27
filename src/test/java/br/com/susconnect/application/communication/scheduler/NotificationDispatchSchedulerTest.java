package br.com.susconnect.application.communication.scheduler;

import br.com.susconnect.communication.application.service.NotificationDispatchService;
import br.com.susconnect.communication.infrastructure.scheduler.NotificationDispatchScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDispatchSchedulerTest {

    @Mock
    private NotificationDispatchService notificationDispatchService;

    private NotificationDispatchScheduler scheduler;

    @BeforeEach
    void setUp() {

        scheduler = new NotificationDispatchScheduler(
                notificationDispatchService
        );
    }

    @Test
    void shouldDispatchPendingNotificationsAutomatically() {

        // Arrange
        when(notificationDispatchService
                .dispatchPendingNotifications())
                .thenReturn(3);

        // Act
        scheduler.dispatchPendingNotifications();

        // Assert
        verify(
                notificationDispatchService,
                times(1)
        ).dispatchPendingNotifications();
    }

    @Test
    void shouldExecuteSuccessfullyWhenThereAreNoPendingNotifications() {

        // Arrange
        when(notificationDispatchService
                .dispatchPendingNotifications())
                .thenReturn(0);

        // Act
        scheduler.dispatchPendingNotifications();

        // Assert
        verify(
                notificationDispatchService,
                times(1)
        ).dispatchPendingNotifications();
    }
}