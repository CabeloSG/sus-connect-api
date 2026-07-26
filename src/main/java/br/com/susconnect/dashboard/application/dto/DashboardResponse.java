package br.com.susconnect.dashboard.application.dto;

import br.com.susconnect.availability.application.dto.AvailableSlotIndicatorsResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a visão consolidada dos principais
 * indicadores operacionais do SUS Connect.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record DashboardResponse(

        @JsonProperty("agendamentos")
        AppointmentIndicatorsResponse appointments,

        @JsonProperty("comunicacoes")
        CommunicationIndicatorsResponse communications,

        @JsonProperty("vagas")
        AvailableSlotIndicatorsResponse availableSlots,

        @JsonProperty("desempenho")
        PerformanceIndicatorsResponse performance

) {
}