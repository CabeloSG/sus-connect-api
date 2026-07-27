package br.com.susconnect.application.availability.usecase;

import br.com.susconnect.availability.application.dto.AvailableSlotIndicatorsResponse;
import br.com.susconnect.availability.application.usecase.GetAvailableSlotIndicatorsUseCase;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários dos indicadores de vagas.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class GetAvailableSlotIndicatorsUseCaseTest {

    @Mock
    private AvailableSlotRepository availableSlotRepository;

    @InjectMocks
    private GetAvailableSlotIndicatorsUseCase useCase;

    @Test
    void shouldReturnAvailableSlotIndicators() {

        when(availableSlotRepository.count())
                .thenReturn(20L);

        when(availableSlotRepository.countByStatus(
                AvailableSlotStatus.AVAILABLE))
                .thenReturn(10L);

        when(availableSlotRepository.countByStatus(
                AvailableSlotStatus.FILLED))
                .thenReturn(7L);

        when(availableSlotRepository.countByStatus(
                AvailableSlotStatus.EXPIRED))
                .thenReturn(3L);

        AvailableSlotIndicatorsResponse result =
                useCase.execute();

        assertEquals(20L, result.getTotalSlots());
        assertEquals(10L, result.getAvailableSlots());
        assertEquals(7L, result.getFilledSlots());
        assertEquals(3L, result.getExpiredSlots());

        verify(availableSlotRepository).count();

        verify(availableSlotRepository)
                .countByStatus(AvailableSlotStatus.AVAILABLE);

        verify(availableSlotRepository)
                .countByStatus(AvailableSlotStatus.FILLED);

        verify(availableSlotRepository)
                .countByStatus(AvailableSlotStatus.EXPIRED);
    }

    @Test
    void shouldReturnZeroIndicatorsWhenThereAreNoSlots() {

        when(availableSlotRepository.count())
                .thenReturn(0L);

        when(availableSlotRepository.countByStatus(
                AvailableSlotStatus.AVAILABLE))
                .thenReturn(0L);

        when(availableSlotRepository.countByStatus(
                AvailableSlotStatus.FILLED))
                .thenReturn(0L);

        when(availableSlotRepository.countByStatus(
                AvailableSlotStatus.EXPIRED))
                .thenReturn(0L);

        AvailableSlotIndicatorsResponse result =
                useCase.execute();

        assertEquals(0L, result.getTotalSlots());
        assertEquals(0L, result.getAvailableSlots());
        assertEquals(0L, result.getFilledSlots());
        assertEquals(0L, result.getExpiredSlots());
    }
}