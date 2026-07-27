package br.com.susconnect.communication.infrastructure.scheduler;

import br.com.susconnect.communication.application.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler responsável por iniciar automaticamente
 * o processamento das notificações pendentes do SUS Connect.
 *
 * No MVP, o processamento ocorre periodicamente e simula
 * o envio das comunicações por WhatsApp, SMS e E-mail.
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
public class NotificationDispatchScheduler {

    private final NotificationDispatchService notificationDispatchService;

    /**
     * Processa periodicamente notificações que estejam
     * aguardando envio.
     */
    @Scheduled(
            fixedDelayString =
                    "${sus-connect.notification.dispatch-delay-ms:10000}"
    )
    public void dispatchPendingNotifications() {

        int processed =
                notificationDispatchService
                        .dispatchPendingNotifications();

        if (processed > 0) {

            log.info(
                    "Processamento automático concluído. Notificações enviadas: {}",
                    processed
            );
        }
    }
}