package br.com.susconnect.communication.application.validator;

import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.PatientResponse;
import br.com.susconnect.communication.infrastructure.persistence.NotificationDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Responsável por validar todas as regras relacionadas
 * ao processamento da resposta do paciente.
 *
 * Este componente centraliza as validações do fluxo
 * de confirmação da comunicação, mantendo o
 * ResponseProcessorService focado apenas na orquestração
 * do processo.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class CommunicationResponseValidator {

    private final NotificationDeliveryRepository notificationDeliveryRepository;

    /**
     * Valida se a notificação pode receber uma resposta.
     *
     * Caso a comunicação já tenha sido respondida por outro
     * canal, informa qual foi o canal utilizado e qual decisão
     * foi registrada anteriormente.
     *
     * @param delivery notificação correspondente ao token informado.
     */
    public void validateStatus(NotificationDelivery delivery) {

        switch (delivery.getStatus()) {

            case SENT, DELIVERED, OPENED -> {
                // Status válidos para receber resposta.
            }

            case CREATED -> throw new BusinessException(
                    "A notificação ainda não foi enviada ao paciente."
            );

            case RESPONDED, INVALIDATED ->
                    throwAlreadyAnsweredException(delivery);

            case FAILED -> throw new BusinessException(
                    "Não foi possível entregar esta notificação ao paciente."
            );

            case EXPIRED -> throw new BusinessException(
                    "Esta notificação expirou."
            );
        }
    }

    /**
     * Verifica se já existe uma notificação respondida
     * pertencente à mesma comunicação.
     *
     * Caso exista, informa a decisão registrada e o canal
     * utilizado pelo paciente.
     *
     * @param currentDelivery notificação utilizada na tentativa atual.
     */
    private void throwAlreadyAnsweredException(
            NotificationDelivery currentDelivery) {

        List<NotificationDelivery> deliveries =
                notificationDeliveryRepository.findByCommunicationId(
                        currentDelivery.getCommunication().getId()
                );

        NotificationDelivery respondedDelivery = deliveries.stream()
                .filter(delivery ->
                        delivery.getStatus() == DeliveryStatus.RESPONDED)
                .findFirst()
                .orElse(null);

        if (respondedDelivery == null) {

            throw new BusinessException(
                    "Esta notificação não está mais disponível para resposta."
            );
        }

        throw new BusinessException(
                buildAlreadyAnsweredMessage(respondedDelivery)
        );
    }

    /**
     * Monta a mensagem de acordo com a resposta
     * anteriormente registrada pelo paciente.
     *
     * @param respondedDelivery notificação que recebeu a resposta válida.
     * @return mensagem explicativa.
     */
    private String buildAlreadyAnsweredMessage(
            NotificationDelivery respondedDelivery) {

        String channel = respondedDelivery.getChannel().name();

        if (respondedDelivery.getPatientResponse() == PatientResponse.YES) {

            return "Este agendamento já foi confirmado anteriormente pelo canal "
                    + channel + ".";
        }

        if (respondedDelivery.getPatientResponse() == PatientResponse.NO) {

            return "Este agendamento já foi recusado anteriormente pelo canal "
                    + channel + ".";
        }

        return "Este agendamento já possui uma resposta registrada pelo canal "
                + channel + ".";
    }

    /**
     * Valida se a comunicação ainda está dentro do prazo
     * para receber resposta do paciente.
     *
     * @param delivery entrega da comunicação.
     */
    public void validateCommunicationExpiration(NotificationDelivery delivery) {

        LocalDateTime expirationDate =
                delivery.getCommunication().getExpirationDate();

        if (isExpired(expirationDate)) {

            throw new BusinessException(
                    "O prazo para confirmação desta consulta expirou."
            );
        }
    }

    private boolean isExpired(LocalDateTime expirationDate) {

        return expirationDate.isBefore(LocalDateTime.now());
    }
}