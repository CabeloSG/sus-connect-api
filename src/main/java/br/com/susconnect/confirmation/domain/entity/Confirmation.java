package br.com.susconnect.confirmation.domain.entity;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.common.entity.BaseEntity;
import br.com.susconnect.confirmation.domain.enums.ConfirmationChannel;
import br.com.susconnect.confirmation.domain.enums.ConfirmationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa a confirmação de um agendamento.
 *
 * Cada confirmação está vinculada a um único agendamento e
 * registra todas as informações relacionadas ao processo
 * de confirmação realizado pelo paciente.
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
@Table(name = "confirmations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Confirmation extends BaseEntity {

    /**
     * Agendamento relacionado à confirmação.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /**
     * Token utilizado para confirmação.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String token;

    /**
     * Situação atual da confirmação.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ConfirmationStatus status = ConfirmationStatus.PENDING;

    /**
     * Canal utilizado para envio.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfirmationChannel channel;

    /**
     * Data e hora em que a solicitação foi enviada.
     */
    @Column(nullable = false)
    private LocalDateTime requestedAt;

    /**
     * Data e hora da confirmação do paciente.
     */
    private LocalDateTime confirmedAt;

    /**
     * Data limite para confirmação.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Endereço IP do dispositivo que realizou a confirmação.
     */
    @Column(length = 45)
    private String ipAddress;

    /**
     * Identificação do navegador ou dispositivo utilizado.
     */
    @Column(length = 255)
    private String userAgent;

}