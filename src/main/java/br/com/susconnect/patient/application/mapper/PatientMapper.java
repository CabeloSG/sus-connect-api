package br.com.susconnect.patient.application.mapper;

import br.com.susconnect.patient.application.dto.CreatePatientRequest;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.dto.UpdatePatientRequest;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.domain.enums.PatientStatus;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre
 * DTOs e entidades do domínio Patient.
 *
 * Centralizar esse processo evita
 * duplicação de código na aplicação.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Component
public class PatientMapper {

    /**
     * Converte um CreatePatientRequest em Patient.
     */
    public Patient toEntity(CreatePatientRequest request) {

        return Patient.builder()

                .fullName(request.getFullName())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .email(request.getEmail())
                .susCard(request.getSusCard())

                // Todo paciente inicia ativo
                .status(PatientStatus.ACTIVE)

                .build();

    }

    /**
     * Atualiza os dados de um paciente existente.
     */
    public void updateEntity(
            Patient patient,
            UpdatePatientRequest request) {

        patient.setFullName(request.getFullName());
        patient.setBirthDate(request.getBirthDate());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setStatus(request.getStatus());

    }

    /**
     * Converte uma entidade Patient
     * para PatientResponse.
     */
    public PatientResponse toResponse(Patient patient) {

        return PatientResponse.builder()

                .id(patient.getId())
                .fullName(patient.getFullName())
                .cpf(patient.getCpf())
                .birthDate(patient.getBirthDate())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .susCard(patient.getSusCard())
                .status(patient.getStatus())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())

                .build();

    }

}