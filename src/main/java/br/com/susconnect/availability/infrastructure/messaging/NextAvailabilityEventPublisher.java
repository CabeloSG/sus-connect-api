package br.com.susconnect.availability.infrastructure.messaging;

import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicador responsável por enviar ao Apache Kafka
 * eventos de próxima disponibilidade encontrada.
 *
 * O evento permite que outros componentes reajam à
 * disponibilidade encontrada sem acoplamento direto
 * ao fluxo responsável pela consulta.
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
public class NextAvailabilityEventPublisher {

    /**
     * Tópico responsável pelos eventos de
     * próxima disponibilidade encontrada.
     */
    private static final String TOPIC =
            "next-availability-found";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica um evento informando que uma próxima
     * disponibilidade foi encontrada para o paciente.
     *
     * @param event evento contendo os dados da disponibilidade.
     */
    public void publish(NextAvailabilityFoundEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.patientId().toString(),
                event
        );
    }
}