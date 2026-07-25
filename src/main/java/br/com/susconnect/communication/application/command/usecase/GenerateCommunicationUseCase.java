package br.com.susconnect.communication.application.command.usecase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.communication.application.factory.CommunicationFactory;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsável pela geração da comunicação
 * vinculada a um agendamento.
 *
 * A comunicação é criada juntamente com todas as suas
 * notificações de entrega (WhatsApp, SMS e E-mail),
 * sendo persistida em uma única transação.
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
public class GenerateCommunicationUseCase {

    private final CommunicationFactory communicationFactory;

    private final CommunicationRepository communicationRepository;

    /**
     * Gera a comunicação para um agendamento.
     *
     * @param appointment agendamento criado.
     * @return comunicação persistida.
     */
    public Communication execute(Appointment appointment) {

        Communication communication =
                communicationFactory.create(appointment);

        return communicationRepository.save(communication);
    }

}