package br.com.susconnect.availability.domain.enums;

/**
 * Representa os possíveis estados de uma vaga disponibilizada
 * após o cancelamento ou recusa de um agendamento.
 *
 * Uma vaga é criada inicialmente como AVAILABLE e permanece
 * disponível para reaproveitamento pela equipe da unidade de saúde.
 *
 * A vaga poderá ser marcada como FILLED quando o horário for
 * ocupado novamente ou como EXPIRED quando não puder mais ser
 * reaproveitado.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public enum AvailableSlotStatus {

    /**
     * Vaga disponível para reaproveitamento
     * pela equipe da unidade de saúde.
     */
    AVAILABLE,

    /**
     * Vaga preenchida após atuação da equipe
     * responsável pelo agendamento.
     */
    FILLED,

    /**
     * Vaga que não foi reaproveitada antes
     * da data e horário do atendimento.
     */
    EXPIRED

}