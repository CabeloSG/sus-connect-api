package br.com.susconnect.availability.application.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import br.com.susconnect.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsável por disponibilizar uma vaga
 * após o cancelamento de um agendamento.
 *
 * Quando um paciente recusa um atendimento, o agendamento
 * original é cancelado e o horário correspondente pode ser
 * disponibilizado para reaproveitamento pela unidade de saúde.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReleaseAvailableSlotUseCase {

    private final AvailableSlotRepository availableSlotRepository;

    /**
     * Libera uma vaga correspondente ao agendamento cancelado.
     *
     * @param appointment agendamento que originou a vaga.
     * @return vaga disponibilizada para reaproveitamento.
     */
    public AvailableSlot execute(Appointment appointment) {

        validateAppointment(appointment);

        validateSlotDoesNotExist(appointment);

        AvailableSlot availableSlot = buildAvailableSlot(appointment);

        return availableSlotRepository.save(availableSlot);
    }

    /**
     * Valida se o agendamento informado pode originar uma vaga.
     *
     * @param appointment agendamento a ser validado.
     */
    private void validateAppointment(Appointment appointment) {

        if (appointment == null || appointment.getId() == null) {
            throw new BusinessException(
                    "Agendamento inválido para liberação da vaga."
            );
        }
    }

    /**
     * Impede que um mesmo agendamento origine
     * mais de uma vaga disponível.
     *
     * @param appointment agendamento de origem.
     */
    private void validateSlotDoesNotExist(Appointment appointment) {

        if (availableSlotRepository
                .existsBySourceAppointmentId(appointment.getId())) {

            throw new BusinessException(
                    "Já existe uma vaga liberada para este agendamento."
            );
        }
    }

    /**
     * Constrói a vaga a partir dos dados do
     * agendamento que foi cancelado.
     *
     * @param appointment agendamento de origem.
     * @return nova vaga disponível.
     */
    private AvailableSlot buildAvailableSlot(Appointment appointment) {

        return AvailableSlot.builder()
                .sourceAppointment(appointment)
                .appointmentDateTime(
                        appointment.getAppointmentDateTime())
                .appointmentType(
                        appointment.getAppointmentType())
                .medicalSpecialty(
                        appointment.getMedicalSpecialty())
                .doctor(
                        appointment.getDoctor())
                .healthUnit(
                        appointment.getHealthUnit())
                .status(AvailableSlotStatus.AVAILABLE)
                .build();
    }
}