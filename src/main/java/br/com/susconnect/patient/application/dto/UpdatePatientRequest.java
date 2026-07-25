package br.com.susconnect.patient.application.dto;

import br.com.susconnect.patient.domain.enums.PatientStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO utilizado para atualização dos dados do paciente.
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
@Schema(description = "Dados para atualização de um paciente.")
public class UpdatePatientRequest {

    @Schema(example = "João da Silva")
    private String fullName;

    @Past
    @Schema(example = "1995-08-12")
    private LocalDate birthDate;

    @Schema(example = "67999998888")
    private String phone;

    @Email
    @Schema(example = "joao@email.com")
    private String email;

    @Schema(
            description = "Status do paciente",
            example = "ACTIVE"
    )
    private PatientStatus status;

}