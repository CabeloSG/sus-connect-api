package br.com.susconnect.availability.application.mapper;

import br.com.susconnect.availability.application.dto.AvailableSlotResponse;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import org.springframework.stereotype.Component;

/**
 * Responsável pelo mapeamento entre a entidade AvailableSlot
 * e os DTOs utilizados pela aplicação.
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
public class AvailableSlotMapper {

    /**
     * Converte uma vaga para o DTO de resposta da API.
     *
     * @param availableSlot vaga a ser convertida.
     * @return representação da vaga para resposta.
     */
    public AvailableSlotResponse toResponse(
            AvailableSlot availableSlot) {

        return AvailableSlotResponse.builder()
                .id(availableSlot.getId())
                .sourceAppointmentId(
                        availableSlot.getSourceAppointment().getId())
                .appointmentDateTime(
                        availableSlot.getAppointmentDateTime())
                .appointmentType(
                        availableSlot.getAppointmentType())
                .medicalSpecialty(
                        availableSlot.getMedicalSpecialty())
                .doctor(
                        availableSlot.getDoctor())
                .healthUnit(
                        availableSlot.getHealthUnit())
                .status(
                        availableSlot.getStatus())
                .build();
    }
}