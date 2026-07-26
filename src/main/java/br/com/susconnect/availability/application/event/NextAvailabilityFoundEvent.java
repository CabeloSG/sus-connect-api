package br.com.susconnect.availability.application.event;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento gerado quando uma próxima disponibilidade
 * compatível é encontrada para um paciente cujo
 * agendamento anterior foi recusado/cancelado.
 *
 * O evento possui caráter exclusivamente informativo.
 * A disponibilidade encontrada não representa reserva,
 * confirmação ou criação de um novo agendamento.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record NextAvailabilityFoundEvent(

        UUID patientId,

        UUID cancelledAppointmentId,

        UUID availableSlotId,

        LocalDateTime appointmentDateTime,

        AppointmentType appointmentType,

        MedicalSpecialty medicalSpecialty,

        String doctor,

        String healthUnit

) {
}