package br.com.susconnect.notification.application.service;

import br.com.susconnect.availability.application.event.AvailableSlotReleasedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Serviço responsável por notificar a unidade de saúde
 * quando uma nova vaga é disponibilizada para reaproveitamento.
 *
 * No MVP do Hackathon, o envio de e-mail é simulado através
 * dos logs da aplicação. A implementação poderá futuramente
 * ser substituída por um provedor real de e-mail.
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
public class UnitNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Simula o envio de um alerta para a unidade de saúde
     * informando que existe uma nova vaga disponível.
     *
     * @param event evento referente à vaga liberada.
     */
    public void notifyAvailableSlot(
            AvailableSlotReleasedEvent event) {

        String appointmentDate =
                event.appointmentDateTime()
                        .format(DATE_FORMATTER);

        log.info("""
                
                ====================================================
                SUS CONNECT - ALERTA PARA UNIDADE
                
                Nova vaga disponível para reaproveitamento
                
                Vaga: {}
                Agendamento de origem: {}
                Unidade: {}
                Especialidade: {}
                Tipo: {}
                Médico: {}
                Data/Hora: {}
                
                Ação necessária:
                A equipe da unidade poderá consultar a vaga
                através do dashboard/API do SUS Connect.
                ====================================================
                """,
                event.availableSlotId(),
                event.sourceAppointmentId(),
                event.healthUnit(),
                event.medicalSpecialty(),
                event.appointmentType(),
                event.doctor(),
                appointmentDate
        );
    }
}