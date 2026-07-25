package br.com.susconnect.communication.domain.entity;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.common.entity.BaseEntity;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.domain.enums.PatientResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade responsável por representar uma comunicação
 * gerada para um agendamento do SUS.
 *
 * Uma comunicação pode possuir uma ou mais notificações
 * de entrega (WhatsApp, SMS e E-mail).
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
@Table(name = "communications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Communication extends BaseEntity {

    /**
     * Agendamento relacionado à comunicação.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /**
     * Status atual da comunicação.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunicationStatus status;

    /**
     * Data limite para resposta do paciente.
     */
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    /**
     * Lista de notificações enviadas ao paciente.
     */
    @OneToMany(
            mappedBy = "communication",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Default
    private List<NotificationDelivery> deliveries = new ArrayList<>();

    /**
     * Adiciona uma notificação à comunicação.
     *
     * @param delivery notificação a ser adicionada.
     */
    public void addDelivery(NotificationDelivery delivery) {

        delivery.setCommunication(this);

        this.deliveries.add(delivery);
    }

    /**
     * Atualiza o status da comunicação de acordo com
     * a resposta informada pelo paciente.
     *
     * @param response resposta do paciente.
     */
    public void updateStatusFromPatientResponse(PatientResponse response) {

        if (response == PatientResponse.YES) {
            this.status = CommunicationStatus.CONFIRMED;
            return;
        }

        if (response == PatientResponse.NO) {
            this.status = CommunicationStatus.DECLINED;
        }

    }

}