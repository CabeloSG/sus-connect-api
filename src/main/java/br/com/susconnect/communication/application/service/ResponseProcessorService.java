package br.com.susconnect.communication.application.service;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.availability.application.usecase.ReleaseAvailableSlotUseCase;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.application.command.dto.RegisterPatientResponseRequest;
import br.com.susconnect.communication.application.validator.CommunicationResponseValidator;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.PatientResponse;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import br.com.susconnect.communication.infrastructure.persistence.NotificationDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por processar a resposta enviada pelo paciente
 * para uma comunicação do SUS Connect.
 *
 * Esta classe concentra as regras de negócio relacionadas ao
 * processamento das confirmações enviadas pelos pacientes,
 * independentemente do canal utilizado
 * (WhatsApp, SMS ou E-mail).
 *
 * Quando o paciente recusa o atendimento, além de cancelar
 * o agendamento, o horário correspondente é disponibilizado
 * para reaproveitamento pela unidade de saúde.
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
public class ResponseProcessorService {

    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final CommunicationRepository communicationRepository;
    private final CommunicationResponseValidator communicationResponseValidator;
    private final AppointmentRepository appointmentRepository;
    private final ReleaseAvailableSlotUseCase releaseAvailableSlotUseCase;

    /**
     * Processa a resposta enviada pelo paciente.
     *
     * @param request dados contendo o token da comunicação
     *                e a resposta informada pelo paciente.
     */
    public void process(RegisterPatientResponseRequest request) {

        NotificationDelivery delivery =
                findNotificationDelivery(request.getToken());

        communicationResponseValidator.validateStatus(delivery);

        communicationResponseValidator
                .validateCommunicationExpiration(delivery);

        registerPatientResponse(delivery, request);

        updateCommunication(delivery);

        updateAppointment(delivery);

        invalidateRemainingDeliveries(delivery);
    }

    /**
     * Localiza a notificação correspondente ao token informado.
     *
     * @param token token recebido pelo paciente.
     * @return notificação encontrada.
     * @throws ResourceNotFoundException quando o token não existir.
     */
    private NotificationDelivery findNotificationDelivery(String token) {

        return notificationDeliveryRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Entrega da notificação não encontrada para o token informado."
                        ));
    }

    /**
     * Registra a resposta enviada pelo paciente
     * para a notificação correspondente.
     *
     * Após o registro, a notificação passa para o
     * status RESPONDED.
     *
     * @param delivery notificação localizada.
     * @param request resposta enviada pelo paciente.
     */
    private void registerPatientResponse(
            NotificationDelivery delivery,
            RegisterPatientResponseRequest request) {

        delivery.registerResponse(request.getResponse());

        notificationDeliveryRepository.save(delivery);
    }

    /**
     * Atualiza o status da comunicação de acordo com
     * a resposta registrada pelo paciente.
     *
     * @param delivery notificação respondida.
     */
    private void updateCommunication(NotificationDelivery delivery) {

        Communication communication = delivery.getCommunication();

        communication.updateStatusFromPatientResponse(
                delivery.getPatientResponse()
        );

        communicationRepository.save(communication);
    }

    /**
     * Atualiza o status do agendamento conforme
     * a resposta informada pelo paciente.
     *
     * YES -> CONFIRMED
     *
     * NO -> CANCELLED e libera o horário para
     * reaproveitamento pela unidade de saúde.
     *
     * @param delivery notificação respondida.
     */
    private void updateAppointment(NotificationDelivery delivery) {

        Appointment appointment =
                delivery.getCommunication().getAppointment();

        if (delivery.getPatientResponse() == PatientResponse.YES) {

            appointment.confirm();

            appointmentRepository.save(appointment);

            return;
        }

        if (delivery.getPatientResponse() == PatientResponse.NO) {

            appointment.cancel();

            appointmentRepository.save(appointment);

            releaseAvailableSlotUseCase.execute(appointment);
        }
    }

    /**
     * Invalida todas as notificações da comunicação,
     * exceto aquela utilizada pelo paciente para responder.
     *
     * Esta regra garante que apenas uma única resposta
     * seja aceita para a mesma comunicação,
     * independentemente do canal utilizado.
     *
     * @param currentDelivery notificação utilizada
     *                        pelo paciente.
     */
    private void invalidateRemainingDeliveries(
            NotificationDelivery currentDelivery) {

        List<NotificationDelivery> deliveries =
                notificationDeliveryRepository.findByCommunicationId(
                        currentDelivery.getCommunication().getId()
                );

        for (NotificationDelivery delivery : deliveries) {

            if (delivery.getId().equals(currentDelivery.getId())) {
                continue;
            }

            delivery.invalidate();
        }

        notificationDeliveryRepository.saveAll(deliveries);
    }
}