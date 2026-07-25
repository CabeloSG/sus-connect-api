package br.com.susconnect.appointment.domain.enums;

/**
 * Representa os possíveis estados de um agendamento.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public enum AppointmentStatus {

    /**
     * Agendamento criado.
     */
    SCHEDULED,

    /**
     * Aguardando confirmação do paciente.
     */
    PENDING_CONFIRMATION,

    /**
     * Consulta confirmada.
     */
    CONFIRMED,

    /**
     * Consulta cancelada.
     */
    CANCELLED,

    /**
     * Paciente não compareceu.
     */
    NO_SHOW,

    /**
     * Consulta realizada.
     */
    COMPLETED

}