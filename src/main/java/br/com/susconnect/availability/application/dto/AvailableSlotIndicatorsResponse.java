package br.com.susconnect.availability.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa os indicadores operacionais das vagas
 * disponibilizadas para reaproveitamento.
 *
 * Permite que a equipe da unidade de saúde visualize
 * rapidamente a quantidade total de vagas geradas,
 * disponíveis, preenchidas e expiradas.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotIndicatorsResponse {

    /**
     * Quantidade total de vagas geradas
     * para reaproveitamento.
     */
    @JsonProperty("totalVagas")
    private long totalSlots;

    /**
     * Quantidade de vagas atualmente disponíveis
     * para reaproveitamento.
     */
    @JsonProperty("vagasDisponiveis")
    private long availableSlots;

    /**
     * Quantidade de vagas que foram efetivamente
     * reaproveitadas.
     */
    @JsonProperty("vagasPreenchidas")
    private long filledSlots;

    /**
     * Quantidade de vagas que expiraram sem
     * serem reaproveitadas.
     */
    @JsonProperty("vagasExpiradas")
    private long expiredSlots;
}