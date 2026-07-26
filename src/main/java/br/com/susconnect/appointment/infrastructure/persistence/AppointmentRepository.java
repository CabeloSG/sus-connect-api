package br.com.susconnect.appointment.infrastructure.persistence;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.patient.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositório responsável pelo acesso aos dados de agendamentos.
 *
 * Projeto: SUS Connect
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Busca todos os agendamentos de um paciente.
     *
     * @param patient paciente.
     * @return lista de agendamentos.
     */
    List<Appointment> findByPatient(Patient patient);

    /**
     * Busca agendamentos por status.
     *
     * @param status status do agendamento.
     * @return lista de agendamentos.
     */
    List<Appointment> findByStatus(AppointmentStatus status);

    /**
     * Busca agendamentos dentro de um intervalo de datas.
     *
     * @param start data inicial.
     * @param end data final.
     * @return lista de agendamentos.
     */
    List<Appointment> findByAppointmentDateTimeBetween(
            LocalDateTime start,
            LocalDateTime end);

    /**
     * Verifica se já existe um agendamento para o médico
     * na data e horário informados.
     *
     * @param doctor nome do médico.
     * @param appointmentDateTime data e horário.
     * @return true quando existir conflito.
     */
    boolean existsByDoctorAndAppointmentDateTime(
            String doctor,
            LocalDateTime appointmentDateTime);

    /**
     * Verifica se existe algum agendamento vinculado
     * ao paciente informado.
     *
     * @param patientId identificador do paciente.
     * @return true caso exista ao menos um agendamento.
     */
    boolean existsByPatient_Id(UUID patientId);

    /**
     * Conta a quantidade de agendamentos
     * de acordo com o status informado.
     *
     * Utilizado para geração dos indicadores
     * operacionais do dashboard do SUS Connect.
     *
     * @param status status do agendamento.
     * @return quantidade de agendamentos encontrados.
     */
    long countByStatus(AppointmentStatus status);

}