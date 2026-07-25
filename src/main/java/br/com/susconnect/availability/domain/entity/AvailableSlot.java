package br.com.susconnect.availability.domain.entity;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa uma vaga disponibilizada para reaproveitamento
 * após o cancelamento ou recusa de um agendamento.
 *
 * A vaga mantém referência ao agendamento que originou
 * sua liberação, preservando a rastreabilidade do processo.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Entity
@Table(name = "available_slots")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlot extends BaseEntity {

    /**
     * Agendamento que originou a liberação da vaga.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "source_appointment_id",
            nullable = false,
            unique = true
    )
    private Appointment sourceAppointment;

    /**
     * Data e horário disponíveis para atendimento.
     */
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    /**
     * Tipo de atendimento da vaga.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType appointmentType;

    /**
     * Especialidade médica da vaga.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalSpecialty medicalSpecialty;

    /**
     * Profissional responsável pelo horário.
     */
    @Column(nullable = false, length = 120)
    private String doctor;

    /**
     * Unidade de saúde responsável pela vaga.
     */
    @Column(nullable = false, length = 120)
    private String healthUnit;

    /**
     * Estado atual da vaga.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private AvailableSlotStatus status = AvailableSlotStatus.AVAILABLE;

}