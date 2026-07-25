package br.com.susconnect.patient.application.usecase;

import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso responsável por buscar um paciente pelo ID.
 *
 * Projeto: SUS Connect
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 */
@Service
@RequiredArgsConstructor
public class FindPatientByIdUseCase {

    private final PatientRepository repository;

    private final PatientMapper mapper;

    public PatientResponse execute(UUID id) {

        Patient patient = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Paciente não encontrado."));

        return mapper.toResponse(patient);

    }

}