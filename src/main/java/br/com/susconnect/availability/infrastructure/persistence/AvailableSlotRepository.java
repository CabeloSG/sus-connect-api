package br.com.susconnect.availability.infrastructure.persistence;

import br.com.susconnect.availability.domain.entity.AvailableSlot;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório responsável pelo acesso aos dados
 * das vagas disponibilizadas para reaproveitamento.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public interface AvailableSlotRepository
        extends JpaRepository<AvailableSlot, UUID> {

    /**
     * Busca vagas de acordo com o status informado.
     *
     * @param status status da vaga.
     * @return lista de vagas encontradas.
     */
    List<AvailableSlot> findByStatus(AvailableSlotStatus status);

    /**
     * Verifica se já existe uma vaga originada
     * pelo agendamento informado.
     *
     * Evita que o mesmo cancelamento gere
     * mais de uma vaga disponível.
     *
     * @param sourceAppointmentId identificador do agendamento de origem.
     * @return true caso já exista uma vaga para o agendamento.
     */
    boolean existsBySourceAppointmentId(UUID sourceAppointmentId);

    /**
     * Conta a quantidade de vagas de acordo
     * com o status informado.
     *
     * Utilizado para geração dos indicadores
     * operacionais de vagas do SUS Connect.
     *
     * @param status status da vaga.
     * @return quantidade de vagas encontradas.
     */
    long countByStatus(AvailableSlotStatus status);

}