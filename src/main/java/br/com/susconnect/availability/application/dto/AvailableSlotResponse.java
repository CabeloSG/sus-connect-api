package br.com.susconnect.availability.application.dto;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para representar uma vaga disponível
 * nas respostas da API.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {

    private UUID id;

    private UUID sourceAppointmentId;

    private LocalDateTime appointmentDateTime;

    private AppointmentType appointmentType;

    private MedicalSpecialty medicalSpecialty;

    private String doctor;

    private String healthUnit;

    private AvailableSlotStatus status;
}