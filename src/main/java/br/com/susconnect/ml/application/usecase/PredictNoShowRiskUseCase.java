package br.com.susconnect.ml.application.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import br.com.susconnect.ml.application.dto.NoShowPredictionRequest;
import br.com.susconnect.ml.application.dto.NoShowPredictionResponse;
import br.com.susconnect.ml.infrastructure.client.NoShowMlClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso responsável por calcular o risco de ausência
 * de um paciente em determinado agendamento.
 *
 * As informações do agendamento e o histórico do paciente
 * são convertidos nas mesmas features utilizadas durante
 * o treinamento do modelo de Machine Learning.
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
public class PredictNoShowRiskUseCase {

    private final AppointmentRepository appointmentRepository;
    private final CommunicationRepository communicationRepository;
    private final NoShowMlClient noShowMlClient;

    /**
     * Calcula o risco de ausência para um agendamento.
     *
     * @param appointmentId identificador do agendamento.
     * @return resultado produzido pelo modelo de ML.
     */
    public NoShowPredictionResponse execute(UUID appointmentId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agendamento não encontrado."
                        )
                );

        List<Appointment> history =
                appointmentRepository.findByPatient(
                        appointment.getPatient()
                );

        /*
         * Remove o próprio agendamento da análise histórica.
         */
        List<Appointment> previousAppointments = history.stream()
                .filter(item ->
                        !item.getId().equals(appointment.getId())
                )
                .filter(item ->
                        item.getAppointmentDateTime()
                                .isBefore(appointment.getAppointmentDateTime())
                )
                .toList();

        long historicalAppointments =
                previousAppointments.size();

        long historicalNoShows =
                previousAppointments.stream()
                        .filter(item ->
                                item.getStatus()
                                        == AppointmentStatus.NO_SHOW
                        )
                        .count();

        long historicalCompleted =
                previousAppointments.stream()
                        .filter(item ->
                                item.getStatus()
                                        == AppointmentStatus.COMPLETED
                        )
                        .count();

        double historicalNoShowRate =
                historicalAppointments == 0
                        ? 0.0
                        : (double) historicalNoShows
                        / historicalAppointments;

        Communication communication =
                communicationRepository
                        .findByAppointmentId(appointmentId)
                        .orElse(null);

        String confirmationStatus =
                communication == null
                        ? "PENDING"
                        : communication.getStatus().name();

        LocalDateTime appointmentDateTime =
                appointment.getAppointmentDateTime();

        int patientAge =
                Period.between(
                        appointment.getPatient().getBirthDate(),
                        appointmentDateTime.toLocalDate()
                ).getYears();

        int dayOfWeek =
                appointmentDateTime
                        .getDayOfWeek()
                        .getValue();

        int hourOfDay =
                appointmentDateTime.getHour();

        /*
         * Para uma predição operacional, calculamos a antecedência
         * em relação ao momento atual.
         */
        long calculatedDaysInAdvance =
                ChronoUnit.DAYS.between(
                        appointment.getCreatedAt().toLocalDate(),
                        appointmentDateTime.toLocalDate()
                );

        int daysInAdvance =
                (int) Math.max(
                        calculatedDaysInAdvance,
                        0
                );

        NoShowPredictionRequest request =
                new NoShowPredictionRequest(
                        patientAge,
                        dayOfWeek,
                        hourOfDay,
                        daysInAdvance,
                        appointment.getAppointmentType().name(),
                        appointment.getMedicalSpecialty().name(),
                        historicalAppointments,
                        historicalNoShows,
                        historicalCompleted,
                        historicalNoShowRate,
                        confirmationStatus
                );

        return noShowMlClient.predict(request);
    }
}