package br.com.susconnect.confirmation.application.dto;

import br.com.susconnect.confirmation.domain.enums.ConfirmationChannel;
import br.com.susconnect.confirmation.domain.enums.ConfirmationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de retorno das confirmações.
 *
 * Projeto: SUS Connect
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationResponse {

    private UUID id;

    private UUID appointmentId;

    private String token;

    private ConfirmationStatus status;

    private ConfirmationChannel channel;

    private LocalDateTime requestedAt;

    private LocalDateTime confirmedAt;

    private LocalDateTime expiresAt;

}