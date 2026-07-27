package br.com.susconnect.patient.application.usecase;

import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.dto.UpdatePatientRequest;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso responsável pela atualização de pacientes.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UpdatePatientUseCase {

    private final PatientRepository repository;

    private final PatientMapper mapper;

    public PatientResponse execute(UUID id, UpdatePatientRequest request) {

        Patient patient = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Paciente não encontrado."));

        mapper.updateEntity(patient, request);

        patient = repository.save(patient);

        return mapper.toResponse(patient);

    }

}