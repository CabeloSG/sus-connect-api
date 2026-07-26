package br.com.susconnect.availability.infrastructure.messaging;

import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicador responsável por enviar eventos relacionados
 * às vagas disponíveis para o Apache Kafka.
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
public class AvailableSlotEventPublisher {

    /**
     * Tópico responsável pelos eventos de liberação de vagas.
     */
    private static final String TOPIC =
            "available-slot-released";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica o evento informando que uma nova vaga
     * foi disponibilizada para reaproveitamento.
     *
     * @param event evento contendo os dados da vaga liberada.
     */
    public void publish(AvailableSlotReleasedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.availableSlotId().toString(),
                event
        );
    }
}