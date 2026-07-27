package br.com.susconnect.application.dashboard.usecase;

import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.availability.application.dto.AvailableSlotIndicatorsResponse;
import br.com.susconnect.availability.application.usecase.GetAvailableSlotIndicatorsUseCase;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import br.com.susconnect.dashboard.application.dto.DashboardResponse;
import br.com.susconnect.dashboard.application.usecase.GetDashboardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela geração dos indicadores do Dashboard.
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
class GetDashboardUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CommunicationRepository communicationRepository;

    @Mock
    private GetAvailableSlotIndicatorsUseCase
            getAvailableSlotIndicatorsUseCase;

    @InjectMocks
    private GetDashboardUseCase useCase;

    private AvailableSlotIndicatorsResponse availableSlotIndicators;

    @BeforeEach
    void setUp() {

        availableSlotIndicators =
                AvailableSlotIndicatorsResponse.builder()
                        .totalSlots(10)
                        .availableSlots(2)
                        .filledSlots(6)
                        .expiredSlots(2)
                        .build();
    }

    @Test
    void shouldBuildDashboardIndicatorsSuccessfully() {

        /*
         * Agendamentos
         */
        when(appointmentRepository.count())
                .thenReturn(20L);

        when(appointmentRepository.countByStatus(
                AppointmentStatus.SCHEDULED))
                .thenReturn(2L);

        when(appointmentRepository.countByStatus(
                AppointmentStatus.PENDING_CONFIRMATION))
                .thenReturn(3L);

        when(appointmentRepository.countByStatus(
                AppointmentStatus.CONFIRMED))
                .thenReturn(5L);

        when(appointmentRepository.countByStatus(
                AppointmentStatus.CANCELLED))
                .thenReturn(2L);

        when(appointmentRepository.countByStatus(
                AppointmentStatus.NO_SHOW))
                .thenReturn(2L);

        when(appointmentRepository.countByStatus(
                AppointmentStatus.COMPLETED))
                .thenReturn(6L);

        /*
         * Comunicações
         */
        when(communicationRepository.count())
                .thenReturn(10L);

        when(communicationRepository.countByStatus(
                CommunicationStatus.PENDING))
                .thenReturn(2L);

        when(communicationRepository.countByStatus(
                CommunicationStatus.CONFIRMED))
                .thenReturn(6L);

        when(communicationRepository.countByStatus(
                CommunicationStatus.DECLINED))
                .thenReturn(2L);

        when(communicationRepository.countByStatus(
                CommunicationStatus.EXPIRED))
                .thenReturn(0L);

        when(communicationRepository.countByStatus(
                CommunicationStatus.CANCELLED))
                .thenReturn(0L);

        when(getAvailableSlotIndicatorsUseCase.execute())
                .thenReturn(availableSlotIndicators);

        DashboardResponse response = useCase.execute();

        assertNotNull(response);
        assertNotNull(response.appointments());
        assertNotNull(response.communications());
        assertNotNull(response.availableSlots());
        assertNotNull(response.performance());

        /*
         * Agendamentos
         */
        assertEquals(
                20L,
                response.appointments().totalAppointments()
        );

        assertEquals(
                2L,
                response.appointments().scheduledAppointments()
        );

        assertEquals(
                3L,
                response.appointments().pendingConfirmationAppointments()
        );

        assertEquals(
                5L,
                response.appointments().confirmedAppointments()
        );

        assertEquals(
                2L,
                response.appointments().cancelledAppointments()
        );

        assertEquals(
                2L,
                response.appointments().noShowAppointments()
        );

        assertEquals(
                6L,
                response.appointments().completedAppointments()
        );

        /*
         * Comunicações
         */
        assertEquals(
                10L,
                response.communications().totalCommunications()
        );

        assertEquals(
                6L,
                response.communications().confirmedCommunications()
        );

        assertEquals(
                2L,
                response.communications().declinedCommunications()
        );

        /*
         * Performance
         *
         * confirmation:
         * 6 / (6 + 2) = 75%
         *
         * decline:
         * 2 / 8 = 25%
         *
         * no-show:
         * 2 / (6 + 2) = 25%
         *
         * reutilização:
         * 6 / (6 + 2) = 75%
         */
        assertEquals(
                75.0,
                response.performance().confirmationRate()
        );

        assertEquals(
                25.0,
                response.performance().declineRate()
        );

        assertEquals(
                25.0,
                response.performance().noShowRate()
        );

        assertEquals(
                75.0,
                response.performance().slotReuseRate()
        );

        verify(getAvailableSlotIndicatorsUseCase)
                .execute();
    }

    @Test
    void shouldReturnZeroRatesWhenThereAreNoConcludedOperations() {

        /*
         * Todos os counts não configurados pelo Mockito
         * retornam 0 por padrão.
         */

        AvailableSlotIndicatorsResponse emptySlots =
                AvailableSlotIndicatorsResponse.builder()
                        .totalSlots(0)
                        .availableSlots(0)
                        .filledSlots(0)
                        .expiredSlots(0)
                        .build();

        when(getAvailableSlotIndicatorsUseCase.execute())
                .thenReturn(emptySlots);

        DashboardResponse response = useCase.execute();

        assertNotNull(response);

        assertEquals(
                0.0,
                response.performance().confirmationRate()
        );

        assertEquals(
                0.0,
                response.performance().declineRate()
        );

        assertEquals(
                0.0,
                response.performance().noShowRate()
        );

        assertEquals(
                0.0,
                response.performance().slotReuseRate()
        );
    }
}