package br.com.susconnect.notification.application.service;

import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import br.com.susconnect.patient.domain.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Serviço responsável por simular o envio de uma comunicação
 * informativa ao paciente quando uma próxima disponibilidade
 * compatível é encontrada.
 *
 * No MVP do Hackathon, WhatsApp, SMS e e-mail são simulados
 * através dos logs da aplicação.
 *
 * A comunicação não representa reserva, confirmação
 * ou criação de um novo agendamento.
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
@Slf4j
public class PatientNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Simula o envio da informação de próxima disponibilidade
     * pelos canais disponíveis do paciente.
     *
     * @param event evento de próxima disponibilidade.
     * @param patient paciente destinatário.
     */
    public void notifyNextAvailability(
            NextAvailabilityFoundEvent event,
            Patient patient) {

        String appointmentDate =
                event.appointmentDateTime()
                        .format(DATE_FORMATTER);

        String message = """
                Próxima disponibilidade identificada:
                %s
                %s
                %s
                %s

                Esta informação não representa reserva ou reagendamento.
                Para solicitar um novo agendamento, procure sua unidade
                de saúde ou utilize os canais oficiais disponíveis.
                """.formatted(
                event.medicalSpecialty(),
                event.healthUnit(),
                event.doctor(),
                appointmentDate
        );

        log.info("""
                
                ====================================================
                SUS CONNECT - COMUNICAÇÃO INFORMATIVA AO PACIENTE
                
                Paciente: {}
                Agendamento cancelado: {}
                Vaga informada: {}
                
                WHATSAPP -> {}
                {}
                
                SMS -> {}
                {}
                
                EMAIL -> {}
                {}
                
                IMPORTANTE:
                Nenhuma vaga foi reservada.
                Nenhum novo agendamento foi criado.
                ====================================================
                """,
                patient.getFullName(),
                event.cancelledAppointmentId(),
                event.availableSlotId(),

                patient.getPhone(),
                message,

                patient.getPhone(),
                message,

                patient.getEmail(),
                message
        );
    }
}