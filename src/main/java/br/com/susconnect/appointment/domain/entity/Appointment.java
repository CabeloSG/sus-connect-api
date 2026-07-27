package br.com.susconnect.appointment.domain.entity;

import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.common.entity.BaseEntity;
import br.com.susconnect.patient.domain.entity.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa um agendamento de atendimento no SUS.
 *
 * Cada agendamento pertence a um paciente e possui
 * uma especialidade, um tipo de atendimento e um
 * ciclo de vida (status).
 *
 * Projeto: SUS Connect
 * Hackathon FIAP
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {

    /**
     * Paciente responsável pelo agendamento.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Data e horário do atendimento.
     */
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    /**
     * Tipo do atendimento.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType appointmentType;

    /**
     * Especialidade médica.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalSpecialty medicalSpecialty;

    /**
     * Nome do profissional responsável.
     */
    @Column(nullable = false, length = 120)
    private String doctor;

    /**
     * Unidade de saúde.
     */
    @Column(nullable = false, length = 120)
    private String healthUnit;

    /**
     * Situação atual do agendamento.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    /**
     * Data limite para confirmação.
     */
    @Column(nullable = false)
    private LocalDateTime confirmationDeadline;

    /**
     * Confirma o agendamento.
     */
    public void confirm() {

        this.status = AppointmentStatus.CONFIRMED;

    }

    /**
     * Cancela o agendamento.
     */
    public void cancel() {

        this.status = AppointmentStatus.CANCELLED;

    }

    /**
     * Registra que o paciente compareceu e
     * o atendimento foi realizado.
     */
    public void complete() {

        this.status = AppointmentStatus.COMPLETED;

    }

    /**
     * Registra que o paciente não compareceu
     * ao atendimento.
     */
    public void markAsNoShow() {

        this.status = AppointmentStatus.NO_SHOW;

    }

}