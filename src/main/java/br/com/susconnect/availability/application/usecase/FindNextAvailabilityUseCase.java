package br.com.susconnect.availability.application.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.availability.application.dto.NextAvailabilityResponse;
import br.com.susconnect.availability.application.mapper.NextAvailabilityMapper;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import br.com.susconnect.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Caso de uso responsável por consultar a próxima
 * disponibilidade compatível após a recusa de um paciente.
 *
 * A consulta possui caráter exclusivamente informativo.
 * Nenhuma vaga é reservada e nenhum novo agendamento
 * é criado durante esta operação.
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
@Transactional(readOnly = true)
public class FindNextAvailabilityUseCase {

    private final AvailableSlotRepository availableSlotRepository;
    private final NextAvailabilityMapper nextAvailabilityMapper;

    /**
     * Busca a próxima vaga disponível compatível com
     * o atendimento recusado pelo paciente.
     *
     * @param appointment agendamento recusado.
     * @return próxima disponibilidade, quando existente.
     */
    public Optional<NextAvailabilityResponse> execute(
            Appointment appointment) {

        validateAppointment(appointment);

        return availableSlotRepository
                .findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
                        AvailableSlotStatus.AVAILABLE,
                        appointment.getAppointmentType(),
                        appointment.getMedicalSpecialty(),
                        appointment.getAppointmentDateTime()
                )
                .map(nextAvailabilityMapper::toResponse);
    }

    /**
     * Valida os dados necessários para realizar
     * a consulta da próxima disponibilidade.
     *
     * @param appointment agendamento recusado.
     */
    private void validateAppointment(Appointment appointment) {

        if (appointment == null) {
            throw new BusinessException(
                    "Agendamento inválido para consulta de disponibilidade."
            );
        }

        if (appointment.getAppointmentDateTime() == null
                || appointment.getAppointmentType() == null
                || appointment.getMedicalSpecialty() == null) {

            throw new BusinessException(
                    "Agendamento não possui os dados necessários para consultar a próxima disponibilidade."
            );
        }
    }
}