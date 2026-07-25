package br.com.susconnect.communication.application.service;

import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.infrastructure.persistence.NotificationDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelo processamento e envio das notificações
 * pendentes do SUS Connect.
 *
 * Nesta versão do MVP, o envio para WhatsApp, SMS e E-mail é
 * simulado. A arquitetura permite que futuramente este serviço
 * seja integrado a provedores externos de comunicação.
 *
 * Uma NotificationDelivery criada inicialmente com status CREATED
 * passa para SENT após a simulação bem-sucedida do envio.
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
@Slf4j
public class NotificationDispatchService {

    private final NotificationDeliveryRepository notificationDeliveryRepository;

    /**
     * Processa todas as notificações que ainda aguardam envio.
     *
     * São consideradas pendentes as entregas que possuem
     * status CREATED.
     *
     * @return quantidade de notificações enviadas.
     */
    @Transactional
    public int dispatchPendingNotifications() {

        List<NotificationDelivery> deliveries =
                notificationDeliveryRepository.findByStatus(
                        DeliveryStatus.CREATED
                );

        deliveries.forEach(this::dispatch);

        return deliveries.size();
    }

    /**
     * Simula o envio de uma notificação.
     *
     * Neste momento não existe integração com um provedor externo.
     * O envio é representado pelo registro no log e pela alteração
     * do estado da entrega de CREATED para SENT.
     *
     * @param delivery notificação que será enviada.
     */
    private void dispatch(NotificationDelivery delivery) {

        log.info(
                "Simulando envio da notificação. Canal: {}, token: {}",
                delivery.getChannel(),
                delivery.getToken()
        );

        delivery.markAsSent();

        notificationDeliveryRepository.save(delivery);

        log.info(
                "Notificação enviada com sucesso. Canal: {}, status: {}",
                delivery.getChannel(),
                delivery.getStatus()
        );
    }
}