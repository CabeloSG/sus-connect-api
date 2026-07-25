package br.com.susconnect.confirmation.application.mapper;

import br.com.susconnect.confirmation.application.dto.ConfirmationResponse;
import br.com.susconnect.confirmation.domain.entity.Confirmation;
import org.springframework.stereotype.Component;

/**
 * Responsável pela conversão entre entidades
 * e DTOs de confirmação.
 *
 * Projeto: SUS Connect
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Component
public class ConfirmationMapper {

    public ConfirmationResponse toResponse(
            Confirmation entity) {

        return ConfirmationResponse.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment().getId())
                .token(entity.getToken())
                .status(entity.getStatus())
                .channel(entity.getChannel())
                .requestedAt(entity.getRequestedAt())
                .confirmedAt(entity.getConfirmedAt())
                .expiresAt(entity.getExpiresAt())
                .build();

    }

}