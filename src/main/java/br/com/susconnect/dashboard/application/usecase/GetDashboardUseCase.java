package br.com.susconnect.dashboard.application.usecase;

import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.availability.application.dto.AvailableSlotIndicatorsResponse;
import br.com.susconnect.availability.application.usecase.GetAvailableSlotIndicatorsUseCase;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import br.com.susconnect.dashboard.application.dto.AppointmentIndicatorsResponse;
import br.com.susconnect.dashboard.application.dto.CommunicationIndicatorsResponse;
import br.com.susconnect.dashboard.application.dto.DashboardResponse;
import br.com.susconnect.dashboard.application.dto.PerformanceIndicatorsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsável por consolidar os principais
 * indicadores operacionais do SUS Connect.
 *
 * Reúne informações dos módulos de agendamento,
 * comunicação e disponibilidade de vagas para fornecer
 * uma visão consolidada da operação.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDashboardUseCase {

    private final AppointmentRepository appointmentRepository;
    private final CommunicationRepository communicationRepository;
    private final GetAvailableSlotIndicatorsUseCase
            getAvailableSlotIndicatorsUseCase;

    /**
     * Gera a visão consolidada do dashboard.
     *
     * @return indicadores operacionais do SUS Connect.
     */
    public DashboardResponse execute() {

        AppointmentIndicatorsResponse appointments =
                buildAppointmentIndicators();

        CommunicationIndicatorsResponse communications =
                buildCommunicationIndicators();

        AvailableSlotIndicatorsResponse availableSlots =
                getAvailableSlotIndicatorsUseCase.execute();

        PerformanceIndicatorsResponse performance =
                buildPerformanceIndicators(
                        appointments,
                        communications,
                        availableSlots
                );

        return new DashboardResponse(
                appointments,
                communications,
                availableSlots,
                performance
        );
    }

    /**
     * Gera os indicadores de agendamentos.
     */
    private AppointmentIndicatorsResponse
    buildAppointmentIndicators() {

        long total =
                appointmentRepository.count();

        long scheduled =
                appointmentRepository.countByStatus(
                        AppointmentStatus.SCHEDULED);

        long pending =
                appointmentRepository.countByStatus(
                        AppointmentStatus.PENDING_CONFIRMATION);

        long confirmed =
                appointmentRepository.countByStatus(
                        AppointmentStatus.CONFIRMED);

        long cancelled =
                appointmentRepository.countByStatus(
                        AppointmentStatus.CANCELLED);

        long noShow =
                appointmentRepository.countByStatus(
                        AppointmentStatus.NO_SHOW);

        long completed =
                appointmentRepository.countByStatus(
                        AppointmentStatus.COMPLETED);

        return new AppointmentIndicatorsResponse(
                total,
                scheduled,
                pending,
                confirmed,
                cancelled,
                noShow,
                completed
        );
    }

    /**
     * Gera os indicadores das comunicações.
     */
    private CommunicationIndicatorsResponse
    buildCommunicationIndicators() {

        long total =
                communicationRepository.count();

        long pending =
                communicationRepository.countByStatus(
                        CommunicationStatus.PENDING);

        long confirmed =
                communicationRepository.countByStatus(
                        CommunicationStatus.CONFIRMED);

        long declined =
                communicationRepository.countByStatus(
                        CommunicationStatus.DECLINED);

        long expired =
                communicationRepository.countByStatus(
                        CommunicationStatus.EXPIRED);

        long cancelled =
                communicationRepository.countByStatus(
                        CommunicationStatus.CANCELLED);

        return new CommunicationIndicatorsResponse(
                total,
                pending,
                confirmed,
                declined,
                expired,
                cancelled
        );
    }

    /**
     * Calcula as métricas de desempenho da plataforma.
     */
    private PerformanceIndicatorsResponse
    buildPerformanceIndicators(
            AppointmentIndicatorsResponse appointments,
            CommunicationIndicatorsResponse communications,
            AvailableSlotIndicatorsResponse availableSlots) {

        long answeredCommunications =
                communications.confirmedCommunications()
                        + communications.declinedCommunications();

        double confirmationRate =
                percentage(
                        communications.confirmedCommunications(),
                        answeredCommunications
                );

        double declineRate =
                percentage(
                        communications.declinedCommunications(),
                        answeredCommunications
                );

        long concludedAppointments =
                appointments.completedAppointments()
                        + appointments.noShowAppointments();

        double noShowRate =
                percentage(
                        appointments.noShowAppointments(),
                        concludedAppointments
                );

        long concludedSlots =
                availableSlots.getFilledSlots()
                        + availableSlots.getExpiredSlots();

        double slotReuseRate =
                percentage(
                        availableSlots.getFilledSlots(),
                        concludedSlots
                );

        return new PerformanceIndicatorsResponse(
                confirmationRate,
                declineRate,
                noShowRate,
                slotReuseRate
        );
    }

    /**
     * Calcula uma porcentagem evitando divisão por zero.
     */
    private double percentage(long value, long total) {

        if (total == 0) {
            return 0.0;
        }

        return Math.round(
                ((double) value / total) * 10000.0
        ) / 100.0;
    }
}