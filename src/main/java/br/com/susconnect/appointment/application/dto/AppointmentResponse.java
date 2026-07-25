package br.com.susconnect.appointment.application.dto;

import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de retorno de um agendamento.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private UUID id;

    private UUID patientId;

    private LocalDateTime appointmentDateTime;

    private AppointmentType appointmentType;

    private MedicalSpecialty medicalSpecialty;

    private String doctor;

    private String healthUnit;

    private AppointmentStatus status;

    private LocalDateTime confirmationDeadline;

}