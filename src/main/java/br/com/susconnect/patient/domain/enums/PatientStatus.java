package br.com.susconnect.patient.domain.enums;

/**
 * Representa o status do paciente no sistema.
 *
 * O status permite controlar se um paciente está
 * apto para realizar novos agendamentos.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public enum PatientStatus {

    /**
     * Paciente ativo.
     */
    ACTIVE,

    /**
     * Paciente temporariamente inativo.
     */
    INACTIVE,

    /**
     * Paciente bloqueado.
     */
    BLOCKED

}