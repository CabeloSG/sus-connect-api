package br.com.susconnect.availability.infrastructure.persistence;

import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.availability.domain.entity.AvailableSlot;
import br.com.susconnect.availability.domain.enums.AvailableSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    /**
     * Busca a próxima vaga disponível compatível com
     * o tipo de atendimento e a especialidade médica.
     *
     * A consulta considera apenas vagas posteriores
     * à data e hora informadas e retorna a vaga
     * cronologicamente mais próxima.
     *
     * Esta operação é somente informativa e não
     * realiza reserva ou reagendamento.
     *
     * @param status status atual da vaga.
     * @param appointmentType tipo de atendimento.
     * @param medicalSpecialty especialidade médica.
     * @param appointmentDateTime data e hora de referência.
     * @return próxima vaga disponível, quando existente.
     */
    Optional<AvailableSlot>
    findFirstByStatusAndAppointmentTypeAndMedicalSpecialtyAndAppointmentDateTimeAfterOrderByAppointmentDateTimeAsc(
            AvailableSlotStatus status,
            AppointmentType appointmentType,
            MedicalSpecialty medicalSpecialty,
            LocalDateTime appointmentDateTime
    );

}