package br.com.susconnect.availability.domain.enums;

/**
 * Representa os possíveis estados de uma vaga disponibilizada
 * após o cancelamento ou recusa de um agendamento.
 *
 * Uma vaga é criada inicialmente como AVAILABLE e poderá,
 * futuramente, ser reservada ou ocupada durante o processo
 * de reaproveitamento da agenda da unidade de saúde.
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
     * Vaga disponível para reaproveitamento.
     */
    AVAILABLE,

    /**
     * Vaga temporariamente reservada durante
     * o processo de seleção de um paciente.
     */
    RESERVED,

    /**
     * Vaga preenchida por um novo agendamento.
     */
    FILLED

}