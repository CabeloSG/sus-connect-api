package br.com.susconnect.patient.application.usecase;

import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.patient.application.dto.CreatePatientRequest;
import br.com.susconnect.patient.application.dto.PatientResponse;
import br.com.susconnect.patient.application.mapper.PatientMapper;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Caso de uso responsável pelo cadastro de pacientes.
 *
 * Realiza as validações necessárias antes de persistir
 * um novo paciente no banco de dados.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CreatePatientUseCase {

    private final PatientRepository repository;

    private final PatientMapper mapper;

    /**
     * Executa o cadastro de um novo paciente.
     *
     * @param request Dados do paciente.
     * @return Paciente cadastrado.
     */
    public PatientResponse execute(CreatePatientRequest request) {

        validateCpf(request.getCpf());

        validateSusCard(request.getSusCard());

        Patient patient = mapper.toEntity(request);

        patient = repository.save(patient);

        return mapper.toResponse(patient);

    }

    /**
     * Verifica se já existe um paciente com o CPF informado.
     */
    private void validateCpf(String cpf) {

        if (repository.existsByCpf(cpf)) {
            throw new BusinessException(
                    "Já existe um paciente cadastrado com este CPF.");
        }

    }

    /**
     * Verifica se o Cartão SUS já está cadastrado.
     */
    private void validateSusCard(String susCard) {

        if (repository.existsBySusCard(susCard)) {
            throw new BusinessException(
                    "Já existe um paciente cadastrado com este Cartão SUS.");
        }

    }

}