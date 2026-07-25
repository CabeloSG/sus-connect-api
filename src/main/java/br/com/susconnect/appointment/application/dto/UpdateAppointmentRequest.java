package br.com.susconnect.appointment.application.dto;

import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO utilizado para atualização de um agendamento.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de um agendamento.")
public class UpdateAppointmentRequest {

    @Future
    private LocalDateTime appointmentDateTime;

    private String doctor;

    private String healthUnit;

    private AppointmentStatus status;

}