package br.com.susconnect.patient.application.dto;

import br.com.susconnect.patient.domain.enums.PatientStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO retornado pela API contendo os dados
 * de um paciente.
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
@Schema(description = "Dados retornados pela API para um paciente.")
public class PatientResponse {

    private UUID id;

    private String fullName;

    private String cpf;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String susCard;

    private PatientStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}