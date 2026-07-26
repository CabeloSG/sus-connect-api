package br.com.susconnect.availability.infrastructure.messaging;

import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import br.com.susconnect.notification.application.service.UnitNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer responsável por processar eventos de
 * vagas liberadas publicados no Apache Kafka.
 *
 * Ao receber o evento, o consumer aciona o serviço
 * responsável por alertar a unidade de saúde.
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
@Slf4j
public class AvailableSlotReleasedEventConsumer {

    private final UnitNotificationService unitNotificationService;

    /**
     * Consome eventos de liberação de vagas.
     *
     * @param event evento contendo os dados da vaga liberada.
     */
    @KafkaListener(
            topics = "available-slot-released",
            groupId = "sus-connect-availability"
    )
    public void consume(
            AvailableSlotReleasedEvent event) {

        log.info(
                "Evento Kafka recebido. availableSlotId={}",
                event.availableSlotId()
        );

        unitNotificationService.notifyAvailableSlot(event);
    }
}