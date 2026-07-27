package br.com.susconnect.appointment.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para registrar o comparecimento
 * do paciente ao atendimento.
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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registro de comparecimento do paciente.")
public class RegisterAttendanceRequest {

    @NotNull(message = "O comparecimento deve ser informado.")
    @Schema(
            description = "Indica se o paciente compareceu ao atendimento.",
            example = "true"
    )
    private Boolean attended;
}