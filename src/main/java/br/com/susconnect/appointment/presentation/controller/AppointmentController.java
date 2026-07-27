package br.com.susconnect.appointment.presentation.controller;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.dto.CreateAppointmentRequest;
import br.com.susconnect.appointment.application.usecase.CreateAppointmentUseCase;
import br.com.susconnect.appointment.application.usecase.FindAllAppointmentsUseCase;
import br.com.susconnect.appointment.application.usecase.FindAppointmentByIdUseCase;
import br.com.susconnect.appointment.application.dto.RegisterAttendanceRequest;
import br.com.susconnect.appointment.application.usecase.RegisterAttendanceUseCase;
import br.com.susconnect.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelo gerenciamento
 * dos agendamentos do SUS.
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
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos")
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;

    private final FindAppointmentByIdUseCase findAppointmentByIdUseCase;

    private final FindAllAppointmentsUseCase findAllAppointmentsUseCase;

    private final RegisterAttendanceUseCase registerAttendanceUseCase;

    /**
     * Realiza o cadastro de um novo agendamento.
     *
     * @param request dados do agendamento.
     * @return agendamento criado.
     */
    @PostMapping
    @Operation(summary = "Cadastrar agendamento")
    public ResponseEntity<SuccessResponse<AppointmentResponse>> create(
            @Valid @RequestBody CreateAppointmentRequest request) {

        AppointmentResponse response =
                createAppointmentUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(
                        "Agendamento cadastrado com sucesso.",
                        response));
    }

    /**
     * Lista todos os agendamentos.
     *
     * @return lista de agendamentos.
     */
    @GetMapping
    @Operation(summary = "Listar agendamentos")
    public ResponseEntity<SuccessResponse<List<AppointmentResponse>>> findAll() {

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Agendamentos encontrados.",
                        findAllAppointmentsUseCase.execute()));
    }

    /**
     * Busca um agendamento pelo identificador.
     *
     * @param id identificador do agendamento.
     * @return agendamento encontrado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    public ResponseEntity<SuccessResponse<AppointmentResponse>> findById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Agendamento encontrado.",
                        findAppointmentByIdUseCase.execute(id)));
    }

    /**
     * Registra o desfecho do atendimento.
     *
     * O registro somente poderá ocorrer após o horário
     * previsto para a consulta.
     *
     * attended = true  -> COMPLETED
     * attended = false -> NO_SHOW
     *
     * @param id identificador do agendamento.
     * @param request informação de comparecimento.
     * @return agendamento atualizado.
     */
    @PatchMapping("/{id}/attendance")
    @Operation(
            summary = "Registrar comparecimento do paciente",
            description = """
                Registra o desfecho de um atendimento.

                attended = true:
                o paciente compareceu e o agendamento passa
                para COMPLETED.

                attended = false:
                o paciente não compareceu e o agendamento
                passa para NO_SHOW.

                O registro somente pode ser realizado após
                o horário previsto para o atendimento.
                """
    )
    public ResponseEntity<SuccessResponse<AppointmentResponse>>
    registerAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterAttendanceRequest request) {

        AppointmentResponse response =
                registerAttendanceUseCase.execute(id, request);

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Desfecho do atendimento registrado com sucesso.",
                        response
                )
        );
    }

}