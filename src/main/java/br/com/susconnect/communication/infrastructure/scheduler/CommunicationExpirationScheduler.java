package br.com.susconnect.communication.infrastructure.scheduler;

import br.com.susconnect.communication.application.command.usecase.ExpireCommunicationsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler responsável pelo processamento automático
 * das comunicações cujo prazo de resposta expirou.
 *
 * A expiração da comunicação não cancela o agendamento.
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
public class CommunicationExpirationScheduler {

    private final ExpireCommunicationsUseCase
            expireCommunicationsUseCase;

    @Scheduled(
            fixedDelayString =
                    "${sus-connect.communication.expiration-delay-ms:60000}"
    )
    public void expireCommunications() {

        int expired =
                expireCommunicationsUseCase.execute();

        if (expired > 0) {

            log.info(
                    "Comunicações expiradas automaticamente: {}",
                    expired
            );
        }
    }
}