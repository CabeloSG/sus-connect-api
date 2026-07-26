package br.com.susconnect.dashboard.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa os indicadores operacionais relacionados
 * aos agendamentos do SUS Connect.
 *
 * Os indicadores permitem acompanhar a distribuição
 * dos agendamentos de acordo com seus estados atuais.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record AppointmentIndicatorsResponse(

        @JsonProperty("totalAgendamentos")
        long totalAppointments,

        @JsonProperty("agendamentosProgramados")
        long scheduledAppointments,

        @JsonProperty("aguardandoConfirmacao")
        long pendingConfirmationAppointments,

        @JsonProperty("agendamentosConfirmados")
        long confirmedAppointments,

        @JsonProperty("agendamentosCancelados")
        long cancelledAppointments,

        @JsonProperty("ausencias")
        long noShowAppointments,

        @JsonProperty("atendimentosRealizados")
        long completedAppointments

) {
}