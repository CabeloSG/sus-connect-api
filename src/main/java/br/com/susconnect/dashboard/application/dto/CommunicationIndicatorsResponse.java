package br.com.susconnect.dashboard.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa os indicadores operacionais relacionados
 * às comunicações enviadas aos pacientes pelo SUS Connect.
 *
 * Os indicadores permitem acompanhar o processamento
 * das solicitações de confirmação dos agendamentos.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record CommunicationIndicatorsResponse(

        @JsonProperty("totalComunicacoes")
        long totalCommunications,

        @JsonProperty("comunicacoesPendentes")
        long pendingCommunications,

        @JsonProperty("comunicacoesConfirmadas")
        long confirmedCommunications,

        @JsonProperty("comunicacoesRecusadas")
        long declinedCommunications,

        @JsonProperty("comunicacoesExpiradas")
        long expiredCommunications,

        @JsonProperty("comunicacoesCanceladas")
        long cancelledCommunications

) {
}