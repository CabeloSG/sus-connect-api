package br.com.susconnect.appointment.application.dto;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para criação de um agendamento.
 *
 * Projeto: SUS Connect
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um agendamento.")
public class CreateAppointmentRequest {

    @NotNull
    @Schema(description = "Id do paciente")
    private UUID patientId;

    @NotNull
    @Future
    @Schema(example = "2026-08-15T14:30:00")
    private LocalDateTime appointmentDateTime;

    @NotNull
    private AppointmentType appointmentType;

    @NotNull
    private MedicalSpecialty medicalSpecialty;

    @NotBlank
    private String doctor;

    @NotBlank
    private String healthUnit;

}