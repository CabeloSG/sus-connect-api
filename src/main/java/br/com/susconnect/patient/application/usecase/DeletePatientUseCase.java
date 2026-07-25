package br.com.susconnect.patient.application.usecase;

import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso responsável pela exclusão de pacientes.
 *
 * Antes da exclusão são realizadas validações para garantir
 * que o paciente exista e que não possua agendamentos
 * vinculados no sistema.
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
public class DeletePatientUseCase {

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    /**
     * Exclui um paciente.
     *
     * @param id identificador do paciente.
     * @throws ResourceNotFoundException caso o paciente não exista.
     * @throws BusinessException caso o paciente possua agendamentos vinculados.
     */
    public void execute(UUID id) {

        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente não encontrado.");
        }

        if (appointmentRepository.existsByPatient_Id(id)) {
            throw new BusinessException(
                    "Não é possível excluir um paciente que possui agendamentos vinculados."
            );
        }

        patientRepository.deleteById(id);

    }

}