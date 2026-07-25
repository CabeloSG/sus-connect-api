package br.com.susconnect.communication.application.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO responsável por receber os dados necessários
 * para geração de uma comunicação.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCommunicationRequest {

    /**
     * Identificador do agendamento que receberá
     * a comunicação de confirmação.
     */
    @NotNull(message = "O identificador do agendamento é obrigatório.")
    @Schema(
            description = "Identificador do agendamento",
            example = "f8793c37-2792-4219-8a3f-91c511d8c035"
    )
    private UUID appointmentId;
}