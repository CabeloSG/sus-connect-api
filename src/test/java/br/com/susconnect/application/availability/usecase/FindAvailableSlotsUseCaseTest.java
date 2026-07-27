package br.com.susconnect.application.availability.usecase;

import br.com.susconnect.availability.application.dto.AvailableSlotResponse;
import br.com.susconnect.availability.application.mapper.AvailableSlotMapper;
import br.com.susconnect.availability.application.usecase.FindAvailableSlotsUseCase;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários da consulta de vagas disponíveis.
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
class FindAvailableSlotsUseCaseTest {

    @Mock
    private AvailableSlotRepository availableSlotRepository;

    @Mock
    private AvailableSlotMapper availableSlotMapper;

    @InjectMocks
    private FindAvailableSlotsUseCase useCase;

    @Test
    void shouldReturnAvailableSlots() {

        AvailableSlot slot1 = AvailableSlot.builder().build();
        AvailableSlot slot2 = AvailableSlot.builder().build();

        AvailableSlotResponse response1 =
                org.mockito.Mockito.mock(AvailableSlotResponse.class);

        AvailableSlotResponse response2 =
                org.mockito.Mockito.mock(AvailableSlotResponse.class);

        when(availableSlotRepository.findByStatus(
                AvailableSlotStatus.AVAILABLE))
                .thenReturn(List.of(slot1, slot2));

        when(availableSlotMapper.toResponse(slot1))
                .thenReturn(response1);

        when(availableSlotMapper.toResponse(slot2))
                .thenReturn(response2);

        List<AvailableSlotResponse> result =
                useCase.execute();

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(availableSlotRepository)
                .findByStatus(AvailableSlotStatus.AVAILABLE);

        verify(availableSlotMapper)
                .toResponse(slot1);

        verify(availableSlotMapper)
                .toResponse(slot2);
    }

    @Test
    void shouldReturnEmptyListWhenNoAvailableSlotsExist() {

        when(availableSlotRepository.findByStatus(
                AvailableSlotStatus.AVAILABLE))
                .thenReturn(List.of());

        List<AvailableSlotResponse> result =
                useCase.execute();

        assertTrue(result.isEmpty());

        verify(availableSlotRepository)
                .findByStatus(AvailableSlotStatus.AVAILABLE);

        verify(availableSlotMapper, never())
                .toResponse(org.mockito.ArgumentMatchers.any());
    }
}