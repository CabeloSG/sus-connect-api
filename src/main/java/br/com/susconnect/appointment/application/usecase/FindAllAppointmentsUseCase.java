package br.com.susconnect.appointment.application.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAllAppointmentsUseCase {

    private final AppointmentRepository repository;

    private final AppointmentMapper mapper;

    public List<AppointmentResponse> execute() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

    }

}