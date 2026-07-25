package br.com.susconnect.appointment.application.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.dto.CreateAppointmentRequest;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.application.command.usecase.GenerateCommunicationUseCase;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsável pelo cadastro de um agendamento.
 *
 * Projeto: SUS Connect
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    private final PatientRepository patientRepository;

    private final AppointmentMapper mapper;

    private final GenerateCommunicationUseCase generateCommunicationUseCase;

    public AppointmentResponse execute(CreateAppointmentRequest request) {

        if (appointmentRepository.existsByDoctorAndAppointmentDateTime(
                request.getDoctor(),
                request.getAppointmentDateTime())) {

            throw new BusinessException(
                    "Já existe um agendamento para este médico nesse horário.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Paciente não encontrado."));

        Appointment appointment = mapper.toEntity(request);

        appointment.setPatient(patient);

        appointment = appointmentRepository.save(appointment);

        generateCommunicationUseCase.execute(appointment);

        return mapper.toResponse(appointment);

    }

}