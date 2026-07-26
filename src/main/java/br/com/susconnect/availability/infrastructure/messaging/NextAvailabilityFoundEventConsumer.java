package br.com.susconnect.availability.infrastructure.messaging;

import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import br.com.susconnect.notification.application.service.PatientNotificationService;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer responsável por processar eventos de
 * próxima disponibilidade encontrada.
 *
 * Ao receber o evento, localiza o paciente e aciona
 * o serviço responsável pela comunicação informativa
 * através dos canais simulados do SUS Connect.
 *
 * A comunicação não realiza reserva de vaga
 * nem cria um novo agendamento.
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
public class NextAvailabilityFoundEventConsumer {

    private final PatientRepository patientRepository;

    private final PatientNotificationService patientNotificationService;

    /**
     * Consome o evento de próxima disponibilidade encontrada.
     *
     * @param event evento contendo paciente e disponibilidade.
     */
    @KafkaListener(
            topics = "next-availability-found",
            groupId = "sus-connect-next-availability"
    )
    public void consume(NextAvailabilityFoundEvent event) {

        log.info(
                "Evento Kafka de próxima disponibilidade recebido. " +
                        "patientId={}, availableSlotId={}",
                event.patientId(),
                event.availableSlotId()
        );

        Patient patient =
                patientRepository.findById(event.patientId())
                        .orElse(null);

        if (patient == null) {

            log.warn(
                    "Paciente não encontrado para notificação. patientId={}",
                    event.patientId()
            );

            return;
        }

        patientNotificationService.notifyNextAvailability(
                event,
                patient
        );

        log.info(
                "Comunicação informativa processada. " +
                        "patientId={}, availableSlotId={}",
                event.patientId(),
                event.availableSlotId()
        );
    }
}