package br.com.susconnect.communication.application.command.dto;

import br.com.susconnect.communication.domain.enums.PatientResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO responsável por representar a solicitação
 * de resposta do paciente para uma comunicação.
 *
 * A resposta é identificada por um token único
 * enviado ao paciente por um dos canais de comunicação
 * (WhatsApp, SMS ou E-mail).
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
public class RegisterPatientResponseRequest {

    /**
     * Token único da notificação.
     */
    @NotBlank(message = "O token é obrigatório.")
    private String token;

    /**
     * Resposta informada pelo paciente.
     */
    @NotNull(message = "A resposta é obrigatória.")
    private PatientResponse response;

}