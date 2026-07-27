package br.com.susconnect.application.communication.scheduler;

import br.com.susconnect.communication.application.command.usecase.ExpireCommunicationsUseCase;
import br.com.susconnect.communication.infrastructure.scheduler.CommunicationExpirationScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunicationExpirationSchedulerTest {

    @Mock
    private ExpireCommunicationsUseCase expireCommunicationsUseCase;

    private CommunicationExpirationScheduler scheduler;

    @BeforeEach
    void setUp() {

        scheduler = new CommunicationExpirationScheduler(
                expireCommunicationsUseCase
        );
    }

    @Test
    void shouldProcessExpiredCommunicationsAutomatically() {

        when(expireCommunicationsUseCase.execute())
                .thenReturn(2);

        scheduler.expireCommunications();

        verify(
                expireCommunicationsUseCase,
                times(1)
        ).execute();
    }

    @Test
    void shouldExecuteSuccessfullyWhenThereAreNoExpiredCommunications() {

        when(expireCommunicationsUseCase.execute())
                .thenReturn(0);

        scheduler.expireCommunications();

        verify(
                expireCommunicationsUseCase,
                times(1)
        ).execute();
    }
}
