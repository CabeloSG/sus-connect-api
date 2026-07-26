package br.com.susconnect.communication.infrastructure.persistence;

import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommunicationRepository
        extends JpaRepository<Communication, UUID> {

    Optional<Communication> findByAppointmentId(UUID appointmentId);

    List<Communication> findByStatus(CommunicationStatus status);

    /**
     * Conta a quantidade de comunicações
     * de acordo com o status informado.
     *
     * Utilizado para geração dos indicadores
     * operacionais do dashboard do SUS Connect.
     *
     * @param status status da comunicação.
     * @return quantidade de comunicações encontradas.
     */
    long countByStatus(CommunicationStatus status);

}