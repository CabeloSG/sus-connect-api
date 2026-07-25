package br.com.susconnect.communication.application.factory;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.communication.application.service.ExpirationPolicyService;
import br.com.susconnect.communication.application.service.TokenGeneratorService;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory responsável pela criação do agregado Communication.
 *
 * Esta classe encapsula toda a lógica necessária para criar
 * uma comunicação válida juntamente com suas notificações
 * (WhatsApp, SMS e E-mail).
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class CommunicationFactory {

    private final TokenGeneratorService tokenGeneratorService;

    private final ExpirationPolicyService expirationPolicyService;

    /**
     * Cria uma comunicação completa para um agendamento.
     *
     * @param appointment agendamento.
     * @return comunicação pronta para persistência.
     */
    public Communication create(Appointment appointment) {

        Communication communication = createCommunication(appointment);

        communication.addDelivery(
                createDelivery(communication, NotificationChannel.WHATSAPP));

        communication.addDelivery(
                createDelivery(communication, NotificationChannel.SMS));

        communication.addDelivery(
                createDelivery(communication, NotificationChannel.EMAIL));

        return communication;
    }

    /**
     * Cria a entidade Communication.
     *
     * @param appointment agendamento.
     * @return comunicação.
     */
    private Communication createCommunication(Appointment appointment) {

        return Communication.builder()
                .appointment(appointment)
                .status(CommunicationStatus.PENDING)
                .expirationDate(
                        expirationPolicyService.calculateExpiration(appointment))
                .build();
    }

    /**
     * Cria uma notificação de entrega.
     *
     * @param communication comunicação.
     * @param channel canal de envio.
     * @return notificação.
     */
    private NotificationDelivery createDelivery(
            Communication communication,
            NotificationChannel channel) {

        return NotificationDelivery.builder()
                .communication(communication)
                .channel(channel)
                .token(tokenGeneratorService.generate())
                .status(DeliveryStatus.CREATED)
                .build();
    }

}