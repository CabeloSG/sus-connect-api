package br.com.susconnect.ml.presentation.controller;

import br.com.susconnect.ml.application.dto.NoShowPredictionResponse;
import br.com.susconnect.ml.application.usecase.PredictNoShowRiskUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller responsável pela consulta do risco de ausência
 * de pacientes em agendamentos do SUS Connect.
 *
 * A predição utiliza o modelo de Machine Learning treinado
 * para estimar a probabilidade de no-show de um agendamento.
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
@RequestMapping("/api/v1/no-show-risk")
@RequiredArgsConstructor
@Tag(
        name = "Machine Learning - Risco de Ausência",
        description = "Predição do risco de não comparecimento aos agendamentos."
)
public class NoShowRiskController {

    private final PredictNoShowRiskUseCase predictNoShowRiskUseCase;

    /**
     * Calcula o risco de ausência para determinado agendamento.
     *
     * @param appointmentId identificador do agendamento.
     * @return probabilidade e classificação do risco.
     */
    @GetMapping("/{appointmentId}")
    @Operation(
            summary = "Calcular risco de ausência",
            description = """
                    Utiliza o modelo de Machine Learning do SUS Connect
                    para estimar a probabilidade de o paciente não
                    comparecer ao agendamento.
                    """
    )
    public ResponseEntity<NoShowPredictionResponse> predict(
            @PathVariable UUID appointmentId) {

        NoShowPredictionResponse response =
                predictNoShowRiskUseCase.execute(appointmentId);

        return ResponseEntity.ok(response);
    }
}