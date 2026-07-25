package br.com.susconnect.communication.presentation.controller;

import br.com.susconnect.common.response.SuccessResponse;
import br.com.susconnect.communication.application.command.dto.RegisterPatientResponseRequest;
import br.com.susconnect.communication.application.command.usecase.RegisterPatientResponseUseCase;
import br.com.susconnect.communication.application.service.NotificationDispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por disponibilizar os endpoints REST
 * relacionados às comunicações enviadas aos pacientes.
 *
 * Disponibiliza operações para simulação do envio das notificações
 * e para o registro da resposta do paciente referente à confirmação
 * de um agendamento do SUS Connect.
 *
 * O controller possui apenas a responsabilidade de receber as
 * requisições HTTP e delegar o processamento aos serviços e casos
 * de uso da camada de aplicação.
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
@RequestMapping("/api/v1/communications")
@RequiredArgsConstructor
@Tag(
        name = "Comunicações",
        description = "Gerenciamento das comunicações enviadas aos pacientes."
)
public class CommunicationController {

    /**
     * Caso de uso responsável por registrar a resposta enviada
     * pelo paciente para uma comunicação.
     */
    private final RegisterPatientResponseUseCase registerPatientResponseUseCase;

    /**
     * Serviço responsável pelo processamento e envio simulado
     * das notificações pendentes.
     */
    private final NotificationDispatchService notificationDispatchService;

    /**
     * Processa todas as notificações pendentes de envio.
     *
     * Nesta versão do MVP, o envio para WhatsApp, SMS e E-mail
     * é simulado. As notificações com status CREATED passam
     * para SENT após o processamento.
     *
     * @return quantidade de notificações processadas.
     */
    @PostMapping("/dispatch")
    @Operation(
            summary = "Disparar notificações pendentes",
            description = """
                    Processa todas as notificações que ainda estão
                    aguardando envio.

                    Nesta versão do MVP, o envio para WhatsApp,
                    SMS e E-mail é simulado.

                    As notificações com status CREATED passam para
                    o status SENT após o processamento.
                    """
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Notificações processadas com sucesso."
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno durante o processamento das notificações."
            )

    })
    public ResponseEntity<SuccessResponse<Integer>> dispatchNotifications() {

        int processedNotifications =
                notificationDispatchService.dispatchPendingNotifications();

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Notificações processadas com sucesso.",
                        processedNotifications
                )
        );
    }

    /**
     * Recebe a resposta enviada pelo paciente para confirmação
     * de um agendamento.
     *
     * O paciente poderá responder utilizando qualquer um dos
     * canais disponíveis (WhatsApp, SMS ou E-mail). Após a
     * validação da requisição, o processamento é delegado ao
     * caso de uso responsável pelo fluxo completo da operação.
     *
     * @param request dados contendo o token da comunicação
     *                e a resposta enviada pelo paciente.
     * @return resposta indicando o sucesso do processamento.
     */
    @PostMapping("/respond")
    @Operation(
            summary = "Registrar resposta de confirmação do paciente",
            description = """
                    Recebe a resposta enviada pelo paciente referente
                    à confirmação de um agendamento do SUS Connect.

                    A resposta poderá ser enviada por qualquer canal
                    de comunicação disponível (WhatsApp, SMS ou E-mail),
                    sendo aceita apenas a primeira resposta válida.
                    """
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Resposta registrada com sucesso."
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "A comunicação não pode ser processada por estar expirada, já respondida ou conter dados inválidos."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Nenhuma comunicação foi encontrada para o token informado."
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor."
            )

    })
    public ResponseEntity<SuccessResponse<Void>> registerPatientResponse(

            @Valid
            @RequestBody
            RegisterPatientResponseRequest request) {

        registerPatientResponseUseCase.execute(request);

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Resposta registrada com sucesso.",
                        null
                )
        );
    }
}