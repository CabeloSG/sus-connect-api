package br.com.susconnect.availability.application.usecase;

import br.com.susconnect.availability.application.dto.AvailableSlotResponse;
import br.com.susconnect.availability.application.mapper.AvailableSlotMapper;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import br.com.susconnect.availability.infrastructure.persistence.AvailableSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso responsável pela consulta das vagas
 * disponíveis para reaproveitamento.
 *
 * Retorna somente vagas que estejam com status AVAILABLE,
 * permitindo que horários liberados após cancelamentos
 * possam ser consultados pela API.
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
public class FindAvailableSlotsUseCase {

    private final AvailableSlotRepository availableSlotRepository;

    private final AvailableSlotMapper availableSlotMapper;

    /**
     * Busca todas as vagas atualmente disponíveis.
     *
     * @return lista de vagas disponíveis.
     */
    public List<AvailableSlotResponse> execute() {

        return availableSlotRepository
                .findByStatus(AvailableSlotStatus.AVAILABLE)
                .stream()
                .map(availableSlotMapper::toResponse)
                .toList();
    }
}