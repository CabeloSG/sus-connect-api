package br.com.susconnect.availability.presentation.controller;

import br.com.susconnect.availability.application.dto.AvailableSlotResponse;
import br.com.susconnect.availability.application.usecase.FindAvailableSlotsUseCase;
import br.com.susconnect.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.susconnect.availability.application.dto.AvailableSlotIndicatorsResponse;
import br.com.susconnect.availability.application.usecase.GetAvailableSlotIndicatorsUseCase;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.availability.application.dto.NextAvailabilityResponse;
import br.com.susconnect.availability.application.usecase.FindNextAvailabilityUseCase;
import br.com.susconnect.common.exception.ResourceNotFoundException;

import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.common.exception.BusinessException;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

/**
 * Controller responsável pela consulta das vagas
 * disponibilizadas para reaproveitamento.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/available-slots")
@RequiredArgsConstructor
@Tag(
        name = "Vagas Disponíveis",
        description = "Consulta de horários liberados para reaproveitamento."
)
public class AvailableSlotController {

    private final FindAvailableSlotsUseCase findAvailableSlotsUseCase;

    private final GetAvailableSlotIndicatorsUseCase getAvailableSlotIndicatorsUseCase;

    private final FindNextAvailabilityUseCase findNextAvailabilityUseCase;

    private final AppointmentRepository appointmentRepository;

    /**
     * Retorna todas as vagas atualmente disponíveis
     * para reaproveitamento.
     *
     * @return lista de vagas disponíveis.
     */
    @GetMapping
    @Operation(
            summary = "Listar vagas disponíveis",
            description = """
                Retorna os horários que foram liberados após
                cancelamento ou recusa de pacientes e que estão
                disponíveis para reaproveitamento.
                """
    )
    public ResponseEntity<SuccessResponse<List<AvailableSlotResponse>>>
    findAvailableSlots() {

        List<AvailableSlotResponse> slots =
                findAvailableSlotsUseCase.execute();

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Vagas disponíveis consultadas com sucesso.",
                        slots
                )
        );
    }

    /**
     * Retorna os indicadores operacionais das vagas
     * disponibilizadas para reaproveitamento.
     *
     * @return indicadores das vagas.
     */
    @GetMapping("/indicators")
    @Operation(
            summary = "Consultar indicadores de vagas",
            description = """
                Retorna a quantidade de vagas disponíveis,
                preenchidas e expiradas, permitindo o acompanhamento
                operacional pela equipe da unidade de saúde.
                """
    )
    public ResponseEntity<SuccessResponse<AvailableSlotIndicatorsResponse>>
    getIndicators() {

        AvailableSlotIndicatorsResponse indicators =
                getAvailableSlotIndicatorsUseCase.execute();

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Indicadores de vagas consultados com sucesso.",
                        indicators
                )
        );
    }

    /**
     * Consulta a próxima disponibilidade compatível
     * com um agendamento recusado pelo paciente.
     *
     * A operação possui caráter exclusivamente informativo.
     * Nenhuma vaga é reservada e nenhum novo agendamento
     * é criado.
     *
     * @param appointmentId identificador do agendamento recusado.
     * @return próxima disponibilidade encontrada.
     */
    @GetMapping("/next")
    @Operation(
            summary = "Consultar próxima disponibilidade",
            description = """
            Consulta a próxima vaga disponível compatível com
            o tipo de atendimento e a especialidade do
            agendamento informado.

            A consulta é exclusivamente informativa.
            A vaga encontrada não é reservada e nenhum novo
            agendamento é criado para o paciente.
            """
    )
    public ResponseEntity<SuccessResponse<NextAvailabilityResponse>>
    findNextAvailability(
            @RequestParam UUID appointmentId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agendamento não encontrado."
                                ));

        if (appointment.getStatus() != AppointmentStatus.CANCELLED) {
            throw new BusinessException(
                    "A próxima disponibilidade somente pode ser consultada para um agendamento cancelado."
            );
        }

        Optional<NextAvailabilityResponse> nextAvailability =
                findNextAvailabilityUseCase.execute(appointment);

        return nextAvailability
                .map(response ->
                        ResponseEntity.ok(
                                SuccessResponse.success(
                                        "Próxima disponibilidade encontrada.",
                                        response
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.ok(
                                SuccessResponse.success(
                                        "Não existe próxima disponibilidade compatível no momento.",
                                        null
                                )
                        )
                );
    }
}