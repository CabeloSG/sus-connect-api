package br.com.susconnect.availability.application.mapper;

import br.com.susconnect.availability.application.dto.NextAvailabilityResponse;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável pela conversão de uma vaga disponível
 * para a resposta de próxima disponibilidade.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Component
public class NextAvailabilityMapper {

    /**
     * Converte uma vaga disponível para o DTO utilizado
     * na consulta da próxima disponibilidade.
     *
     * @param availableSlot vaga encontrada.
     * @return dados da próxima disponibilidade.
     */
    public NextAvailabilityResponse toResponse(
            AvailableSlot availableSlot) {

        return new NextAvailabilityResponse(
                availableSlot.getId(),
                availableSlot.getAppointmentDateTime(),
                availableSlot.getAppointmentType(),
                availableSlot.getMedicalSpecialty(),
                availableSlot.getDoctor(),
                availableSlot.getHealthUnit()
        );
    }
}