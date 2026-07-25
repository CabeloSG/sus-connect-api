package br.com.susconnect.patient.application.usecase;

import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso responsável por listar todos os pacientes.
 *
 * Projeto: SUS Connect
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 */
@Service
@RequiredArgsConstructor
public class FindAllPatientsUseCase {

    private final PatientRepository repository;

    private final PatientMapper mapper;

    public List<PatientResponse> execute() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

    }

}