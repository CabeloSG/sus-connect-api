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
}