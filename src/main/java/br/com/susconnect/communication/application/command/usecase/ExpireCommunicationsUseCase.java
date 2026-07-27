package br.com.susconnect.communication.application.command.usecase;

import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso responsável por expirar automaticamente
 * comunicações cujo prazo de resposta foi encerrado.
 *
 * A expiração afeta apenas a comunicação e suas notificações.
 * O agendamento permanece válido, pois a ausência de resposta
 * não representa cancelamento ou ausência do paciente.
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
@Transactional
public class ExpireCommunicationsUseCase {

    private final CommunicationRepository communicationRepository;

    /**
     * Processa comunicações pendentes cujo prazo
     * de resposta já foi encerrado.
     *
     * @return quantidade de comunicações expiradas.
     */
    public int execute() {

        List<Communication> communications =
                communicationRepository
                        .findByStatusAndExpirationDateBefore(
                                CommunicationStatus.PENDING,
                                LocalDateTime.now()
                        );

        communications.forEach(this::expire);

        communicationRepository.saveAll(communications);

        return communications.size();
    }

    private void expire(Communication communication) {

        communication.expire();

        communication.getDeliveries()
                .forEach(delivery -> delivery.expire());
    }
}