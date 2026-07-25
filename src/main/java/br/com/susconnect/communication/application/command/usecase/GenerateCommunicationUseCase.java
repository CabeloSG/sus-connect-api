package br.com.susconnect.communication.application.command.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.application.factory.CommunicationFactory;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso responsável pela geração da comunicação
 * vinculada a um agendamento.
 *
 * A comunicação é criada juntamente com suas notificações
 * de entrega, sendo persistida em uma única transação.
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
public class GenerateCommunicationUseCase {

    private final CommunicationFactory communicationFactory;
    private final CommunicationRepository communicationRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Gera uma comunicação para o agendamento informado.
     *
     * @param appointmentId identificador do agendamento.
     * @return comunicação persistida.
     */
    public Communication execute(UUID appointmentId) {

        Appointment appointment = findAppointment(appointmentId);

        validateCommunicationDoesNotExist(appointmentId);

        Communication communication =
                communicationFactory.create(appointment);

        return communicationRepository.save(communication);
    }

    /**
     * Localiza o agendamento que receberá a comunicação.
     *
     * @param appointmentId identificador do agendamento.
     * @return agendamento encontrado.
     */
    private Appointment findAppointment(UUID appointmentId) {

        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agendamento não encontrado."
                        )
                );
    }

    /**
     * Impede a criação de mais de uma comunicação
     * para o mesmo agendamento.
     *
     * @param appointmentId identificador do agendamento.
     */
    private void validateCommunicationDoesNotExist(UUID appointmentId) {

        if (communicationRepository
                .findByAppointmentId(appointmentId)
                .isPresent()) {

            throw new BusinessException(
                    "Já existe uma comunicação para este agendamento."
            );
        }
    }
}