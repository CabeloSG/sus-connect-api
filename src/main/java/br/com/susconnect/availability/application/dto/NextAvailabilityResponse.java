package br.com.susconnect.availability.application.dto;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa a próxima disponibilidade encontrada
 * após a recusa de um agendamento pelo paciente.
 *
 * A informação possui caráter exclusivamente informativo.
 * A consulta não reserva a vaga e não cria um novo
 * agendamento para o paciente.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record NextAvailabilityResponse(

        UUID availableSlotId,

        LocalDateTime appointmentDateTime,

        AppointmentType appointmentType,

        MedicalSpecialty medicalSpecialty,

        String doctor,

        String healthUnit

) {
}