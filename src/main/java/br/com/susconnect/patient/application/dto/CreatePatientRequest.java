package br.com.susconnect.patient.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO responsável pelo cadastro de um novo paciente.
 *
 * Contém apenas os dados necessários para criação
 * de um paciente no sistema.
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
@Schema(description = "Dados para cadastro de um paciente.")
public class CreatePatientRequest {

    @NotBlank
    @Schema(example = "João da Silva")
    private String fullName;

    @NotBlank
    @Schema(example = "12345678901")
    private String cpf;

    @Past
    @Schema(example = "1995-08-12")
    private LocalDate birthDate;

    @NotBlank
    @Schema(example = "67999998888")
    private String phone;

    @Email
    @Schema(example = "joao@email.com")
    private String email;

    @NotBlank
    @Schema(example = "706123456789000")
    private String susCard;

}