package br.com.susconnect.appointment.application.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindAppointmentByIdUseCase {

    private final AppointmentRepository repository;

    private final AppointmentMapper mapper;

    public AppointmentResponse execute(UUID id) {

        Appointment appointment = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agendamento não encontrado."));

        return mapper.toResponse(appointment);

    }

}