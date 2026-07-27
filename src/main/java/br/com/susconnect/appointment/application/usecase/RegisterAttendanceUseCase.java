package br.com.susconnect.appointment.application.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.dto.RegisterAttendanceRequest;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso responsável por registrar o desfecho
 * do atendimento de um agendamento.
 *
 * Quando o paciente comparece, o agendamento passa
 * para COMPLETED.
 *
 * Quando o paciente não comparece, o agendamento
 * passa para NO_SHOW.
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
public class RegisterAttendanceUseCase {

    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    public AppointmentResponse execute(
            UUID appointmentId,
            RegisterAttendanceRequest request) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agendamento não encontrado."
                                )
                        );

        validateAppointment(appointment);

        if (Boolean.TRUE.equals(request.getAttended())) {

            appointment.complete();

        } else {

            appointment.markAsNoShow();

        }

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    private void validateAppointment(Appointment appointment) {

        if (appointment.getAppointmentDateTime()
                .isAfter(LocalDateTime.now())) {

            throw new BusinessException(
                    "O comparecimento não pode ser registrado antes do horário do atendimento."
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {

            throw new BusinessException(
                    "Não é possível registrar comparecimento para um agendamento cancelado."
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.NO_SHOW) {

            throw new BusinessException(
                    "O desfecho deste atendimento já foi registrado."
            );
        }
    }
}