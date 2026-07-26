package br.com.susconnect.availability.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa os indicadores operacionais das vagas
 * disponibilizadas para reaproveitamento.
 *
 * Permite que a equipe da unidade de saúde visualize
 * rapidamente a quantidade de vagas disponíveis,
 * preenchidas e expiradas.
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
     * Quantidade de vagas atualmente disponíveis
     * para reaproveitamento.
     */
    private long availableSlots;

    /**
     * Quantidade de vagas que foram reaproveitadas
     * pela equipe da unidade de saúde.
     */
    private long filledSlots;

    /**
     * Quantidade de vagas que expiraram sem
     * serem reaproveitadas.
     */
    private long expiredSlots;
}