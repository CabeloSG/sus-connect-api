package br.com.susconnect.appointment.application.mapper;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.dto.CreateAppointmentRequest;
import br.com.susconnect.appointment.application.dto.UpdateAppointmentRequest;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentMapper {

    public Appointment toEntity(CreateAppointmentRequest request) {

        return Appointment.builder()
                .appointmentDateTime(request.getAppointmentDateTime())
                .appointmentType(request.getAppointmentType())
                .medicalSpecialty(request.getMedicalSpecialty())
                .doctor(request.getDoctor())
                .healthUnit(request.getHealthUnit())
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .confirmationDeadline(
                        request.getAppointmentDateTime().minusDays(2))
                .build();

    }

    public AppointmentResponse toResponse(Appointment entity) {

        return AppointmentResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatient().getId())
                .appointmentDateTime(entity.getAppointmentDateTime())
                .appointmentType(entity.getAppointmentType())
                .medicalSpecialty(entity.getMedicalSpecialty())
                .doctor(entity.getDoctor())
                .healthUnit(entity.getHealthUnit())
                .status(entity.getStatus())
                .confirmationDeadline(entity.getConfirmationDeadline())
                .build();

    }

    public void updateEntity(
            Appointment entity,
            UpdateAppointmentRequest request) {

        entity.setAppointmentDateTime(request.getAppointmentDateTime());
        entity.setDoctor(request.getDoctor());
        entity.setHealthUnit(request.getHealthUnit());
        entity.setStatus(request.getStatus());

    }

}