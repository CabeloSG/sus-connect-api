package br.com.susconnect.dashboard.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa os principais indicadores de desempenho
 * operacional do SUS Connect.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record PerformanceIndicatorsResponse(

        @Schema(
                description = "Taxa percentual de confirmações entre as comunicações respondidas.",
                example = "20.0"
        )
        @JsonProperty("taxaConfirmacaoPercentual")
        double confirmationRate,

        @Schema(
                description = "Taxa percentual de recusas entre as comunicações respondidas.",
                example = "80.0"
        )
        @JsonProperty("taxaRecusaPercentual")
        double declineRate,

        @Schema(
                description = "Taxa percentual de ausência entre os atendimentos com desfecho.",
                example = "10.0"
        )
        @JsonProperty("taxaAusenciaPercentual")
        double noShowRate,

        @Schema(
                description = "Taxa percentual de reaproveitamento das vagas com desfecho.",
                example = "75.0"
        )
        @JsonProperty("taxaReaproveitamentoVagasPercentual")
        double slotReuseRate

) {
}