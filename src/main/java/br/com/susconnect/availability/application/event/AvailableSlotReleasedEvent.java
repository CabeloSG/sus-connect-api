package br.com.susconnect.availability.application.event;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento gerado quando uma vaga é liberada
 * para reaproveitamento pela unidade de saúde.
 *
 * O evento contém apenas os dados necessários para que
 * outros componentes possam reagir à liberação da vaga
 * sem depender diretamente da entidade AvailableSlot.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public record AvailableSlotReleasedEvent(

        UUID availableSlotId,

        UUID sourceAppointmentId,

        LocalDateTime appointmentDateTime,

        AppointmentType appointmentType,

        MedicalSpecialty medicalSpecialty,

        String doctor,

        String healthUnit

) {
}