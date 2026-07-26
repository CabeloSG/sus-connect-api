package br.com.susconnect.dashboard.presentation.controller;

import br.com.susconnect.common.response.SuccessResponse;
import br.com.susconnect.dashboard.application.dto.DashboardResponse;
import br.com.susconnect.dashboard.application.usecase.GetDashboardUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pela disponibilização
 * dos indicadores consolidados do SUS Connect.
 *
 * O dashboard fornece uma visão operacional dos
 * agendamentos, comunicações, vagas disponibilizadas
 * para reaproveitamento e métricas de desempenho.
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
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard",
        description = """
                Indicadores operacionais consolidados
                da plataforma SUS Connect.
                """
)
public class DashboardController {

    private final GetDashboardUseCase getDashboardUseCase;

    /**
     * Retorna a visão consolidada dos principais
     * indicadores operacionais da plataforma.
     *
     * @return dashboard consolidado.
     */
    @GetMapping
    @Operation(
            summary = "Consultar dashboard operacional",
            description = """
                    Retorna uma visão consolidada dos indicadores
                    de agendamentos, comunicações, vagas liberadas
                    e métricas de desempenho do SUS Connect.
                    """
    )
    public ResponseEntity<SuccessResponse<DashboardResponse>>
    getDashboard() {

        DashboardResponse dashboard =
                getDashboardUseCase.execute();

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Dashboard consultado com sucesso.",
                        dashboard
                )
        );
    }
}