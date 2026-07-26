package br.com.susconnect.availability.application.usecase;

import br.com.susconnect.availability.application.dto.AvailableSlotIndicatorsResponse;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsável pela geração dos indicadores
 * operacionais das vagas liberadas para reaproveitamento.
 *
 * Os indicadores permitem que a equipe da unidade de saúde
 * acompanhe rapidamente quantas vagas estão disponíveis,
 * preenchidas ou expiradas.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAvailableSlotIndicatorsUseCase {

    private final AvailableSlotRepository availableSlotRepository;

    /**
     * Gera os indicadores atuais das vagas.
     *
     * @return indicadores operacionais.
     */
    public AvailableSlotIndicatorsResponse execute() {

        long totalSlots =
                availableSlotRepository.count();

        long availableSlots =
                availableSlotRepository.countByStatus(
                        AvailableSlotStatus.AVAILABLE
                );

        long filledSlots =
                availableSlotRepository.countByStatus(
                        AvailableSlotStatus.FILLED
                );

        long expiredSlots =
                availableSlotRepository.countByStatus(
                        AvailableSlotStatus.EXPIRED
                );

        return AvailableSlotIndicatorsResponse.builder()
                .totalSlots(totalSlots)
                .availableSlots(availableSlots)
                .filledSlots(filledSlots)
                .expiredSlots(expiredSlots)
                .build();
    }
}