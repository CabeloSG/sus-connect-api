package br.com.susconnect.communication.domain.entity;

import br.com.susconnect.common.entity.BaseEntity;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.NotificationChannel;
import br.com.susconnect.communication.domain.enums.PatientResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade responsável por representar uma notificação enviada
 * ao paciente através de um canal de comunicação.
 *
 * Cada comunicação pode gerar múltiplas notificações,
 * como WhatsApp, SMS e E-mail.
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
@Table(name = "notification_deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDelivery extends BaseEntity {

    /**
     * Comunicação à qual esta notificação pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communication_id", nullable = false)
    private Communication communication;

    /**
     * Canal utilizado para envio.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    /**
     * Token único utilizado para confirmação.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String token;

    /**
     * Status atual da entrega.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    /**
     * Resposta do paciente.
     */
    @Enumerated(EnumType.STRING)
    private PatientResponse patientResponse;

    /**
     * Data/hora do envio.
     */
    private LocalDateTime sentAt;

    /**
     * Data/hora da confirmação de entrega.
     */
    private LocalDateTime deliveredAt;

    /**
     * Data/hora da abertura da mensagem.
     */
    private LocalDateTime openedAt;

    /**
     * Data/hora da resposta do paciente.
     */
    private LocalDateTime respondedAt;

    /**
     * Registra a resposta do paciente para esta notificação.
     *
     * Ao registrar uma resposta, a notificação passa para o
     * status RESPONDED e a data/hora da resposta é registrada.
     *
     * @param response resposta informada pelo paciente.
     */
    public void registerResponse(PatientResponse response){
        this.patientResponse = response;
        this.status = DeliveryStatus.RESPONDED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * Invalida esta notificação.
     */
    public void invalidate() {

        this.status = DeliveryStatus.INVALIDATED;

    }

    /**
     * Registra o envio desta notificação.
     *
     * A notificação somente pode ser enviada quando estiver
     * no status CREATED.
     *
     * Após o envio, o status passa para SENT e a data/hora
     * do envio é registrada.
     */
    public void markAsSent() {

        if (this.status != DeliveryStatus.CREATED) {
            throw new IllegalStateException(
                    "A notificação somente pode ser enviada quando estiver no status CREATED."
            );
        }

        this.status = DeliveryStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

}